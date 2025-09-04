package com.maahi.spry.ControllerServices;

import ch.qos.logback.core.util.StringUtil;
import com.maahi.spry.Services.NotificationService;
import com.maahi.spry.UtilsService.DateUtils;
import com.maahi.spry.model.AvailabiltyStatus;
import com.maahi.spry.model.Book;
import com.maahi.spry.model.request.BookDto;
import com.maahi.spry.model.request.FetchRequest;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class LibraryManagementService {
    private static final Logger log = LoggerFactory.getLogger(LibraryManagementService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotificationService notifier;

    private DateUtils dateUtils = new DateUtils();

    public List<Book> fetchBooks(BookDto request) {
        Query query = new Query();
        List<Book> bookList = new ArrayList<>();

        if (!StringUtil.isNullOrEmpty(request.getIsbn())) {
            query.addCriteria(Criteria.where("_id").is(request.getIsbn()));
        }

        if (!StringUtil.isNullOrEmpty(request.getWriter())) {
            query.addCriteria(Criteria.where("bookName").is(request.getWriter()));
        }

        if (!StringUtil.isNullOrEmpty(request.getPublishedYear())) {
            query.addCriteria(Criteria.where("publishedYear").is(request.getPublishedYear()));
        }

        if (!StringUtil.isNullOrEmpty(request.getTitle())) {
            query.addCriteria(Criteria.where("title").is(request.getTitle()));
        }

        if (!StringUtil.isNullOrEmpty(request.getAvailabilityStatus())) {
            query.addCriteria(Criteria.where("availabilityStatus").is(request.getAvailabilityStatus()));
        }



            bookList = mongoTemplate.find(query, Book.class);

        return bookList;
    }


    public Book updateBook(String isbn, BookDto bookDto) throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(isbn));
        Update update = new Update();
        boolean isAvailibilityUpdated = false;

        if (!StringUtil.isNullOrEmpty(bookDto.getWriter())) {
            update.set("author", bookDto.getWriter());
        }

        if (bookDto.getPublishedYear() != null) {
            if (dateUtils.isValidYear(bookDto.getPublishedYear())) {
                throw new RuntimeException("Invalid Year: " + isbn);
            }
            update.set("publishedYear", bookDto.getPublishedYear());
        }
        if (!StringUtil.isNullOrEmpty(bookDto.getTitle())) {
            update.set("title", bookDto.getTitle());
        }
        if (!StringUtil.isNullOrEmpty(bookDto.getAvailabilityStatus())) {
            update.set("availabilityStatus", bookDto.getAvailabilityStatus());

            if(bookDto.getAvailabilityStatus().equalsIgnoreCase(AvailabiltyStatus.AVAILABLE.toString())){
                Book existingBook = mongoTemplate.findById(isbn, Book.class);
                String oldStatus = existingBook.getAvailabilityStatus().toString();
                if(!oldStatus.equalsIgnoreCase(AvailabiltyStatus.AVAILABLE.toString())){
                    isAvailibilityUpdated= true;
                }
            }
        }


        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(false);
        Book updatedBook = mongoTemplate.findAndModify(query, update, options, Book.class);

        if (ObjectUtils.isEmpty(updatedBook)){
            throw new RuntimeException("Invalid ISBN: " + isbn);
        }

        // Notifying users
        if(isAvailibilityUpdated){
            notifier.notifyWishlistedUsers(updatedBook);
        }
        return updatedBook;
    }

    public Book addBook(BookDto bookDto) throws Exception {
        Book book = new Book();
        book.setBookName(bookDto.getTitle());
        book.setAuthor(bookDto.getWriter());
        book.setAvailabilityStatus(AvailabiltyStatus.AVAILABLE);
        book.setIsbn(bookDto.getIsbn());
        book.setPublishedYear(bookDto.getPublishedYear());

        try {
            return mongoTemplate.insert(book);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Book with ISBN " + bookDto.getIsbn() + "is already exists");
        }
    }


    public boolean removeBook(String isbn) throws Exception {
        Query query = new Query(Criteria.where("_id").is(isbn));
        DeleteResult result = mongoTemplate.remove(query, Book.class);
        return result.getDeletedCount() > 0;
    }

    public List<Book> searchBook(FetchRequest request) throws Exception {
        Query query = new Query();
        if (request.getAuther() != null) {
            Pattern authorPattern = Pattern.compile(".*" + Pattern.quote(request.getAuther().trim()) + ".*", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("author").regex(authorPattern));
        }

        if (request.getTitle() != null) {
            Pattern titlePattern = Pattern.compile(".*" + Pattern.quote(request.getTitle().trim()) + ".*", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("bookName").regex(titlePattern));
        }
        return mongoTemplate.find(query, Book.class);
    }

    public Book addInWishList(String isbn, String userId) {
        Query query = new Query(Criteria.where("_id").is(isbn));
        Update update = new Update().addToSet("wishListUsers", userId);

        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Book updatedBook = mongoTemplate.findAndModify(query, update, options, Book.class);

        if (ObjectUtils.isEmpty(updatedBook)) {
            throw new RuntimeException("Book with ISBN " + isbn + " not found");
        }
        return updatedBook;
    }

}
