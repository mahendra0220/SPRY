package com.maahi.spry.Controller;

import ch.qos.logback.core.util.StringUtil;
import com.maahi.spry.ControllerServices.LibraryManagementService;
import com.maahi.spry.UtilsService.DateUtils;
import com.maahi.spry.model.BaseResponse;
import com.maahi.spry.model.Book;
import com.maahi.spry.model.request.BookDto;
import com.maahi.spry.model.request.FetchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class commonController {

    @Autowired
    private LibraryManagementService libraryManagementService;

    private  DateUtils dateUtils = new DateUtils();

@PostMapping("/getBook")
@ResponseBody
public ResponseEntity<BaseResponse> fetchBook(@RequestBody  BookDto bookDto) {
    if (ObjectUtils.isEmpty(bookDto)) {
        return new ResponseEntity<>(new BaseResponse("request is blank",null),HttpStatus.BAD_REQUEST);
    }
    List<Book> bookList = libraryManagementService.fetchBooks(bookDto);
    if (ObjectUtils.isEmpty(bookList)) {
        return new ResponseEntity<>(new BaseResponse("No Book found",null),HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(new BaseResponse("book list successfully fetched",bookList),HttpStatus.OK);
}

    @PostMapping("/update-book/{isbn}")
    public ResponseEntity<BaseResponse> updateBook(@PathVariable String isbn, @RequestBody BookDto bookDto) throws Exception {

        if (StringUtil.isNullOrEmpty(isbn)) {
            return new ResponseEntity<>(new BaseResponse("isbn is mandatory", null), HttpStatus.BAD_REQUEST);
        }
        if (ObjectUtils.isEmpty(bookDto)) {
            return new ResponseEntity<>(new BaseResponse("bookDto is required",null), HttpStatus.BAD_REQUEST);
        }
        Book book = libraryManagementService.updateBook(isbn, bookDto);
        return new ResponseEntity<>(new BaseResponse("book Details updated successfully",book),HttpStatus.OK);
    }

    @PostMapping("/add-book")
    public ResponseEntity<BaseResponse> addBook(@RequestBody BookDto request) throws Exception {
    if (ObjectUtils.isEmpty(request) || StringUtil.isNullOrEmpty(request.getIsbn())) {
        return new ResponseEntity<>(new BaseResponse("inValid request", null), HttpStatus.BAD_REQUEST);
    }
        if (request.getPublishedYear() != null && !dateUtils.isValidYear(request.getPublishedYear())) {
            return new ResponseEntity<>(new BaseResponse("invalid year", null), HttpStatus.BAD_REQUEST);
        }
        Book book = libraryManagementService.addBook(request);
        return new ResponseEntity<>(new BaseResponse("new Book added successfully",book),HttpStatus.OK);
    }

    @PostMapping("/remove-book/{isbn}")
    public ResponseEntity<BaseResponse> removeBook(@PathVariable String isbn) throws Exception {
        if (StringUtil.isNullOrEmpty(isbn)) {
            return new ResponseEntity<>(new BaseResponse("isbn is mandatory", null), HttpStatus.BAD_REQUEST);
        }
    boolean isdeleted = libraryManagementService.removeBook(isbn);

        return new ResponseEntity<>(new BaseResponse(isdeleted?"Book Removed Successfully":"No Book found",null),HttpStatus.OK);
    }


    @PostMapping("/search-book")
    public ResponseEntity<BaseResponse> searhBook(@RequestBody FetchRequest request) throws Exception {
        if (ObjectUtils.isEmpty(request) || (StringUtil.isNullOrEmpty(request.getAuther()) && StringUtil.isNullOrEmpty(request.getTitle()))) {
            return new ResponseEntity<>(new BaseResponse("inValid request", null), HttpStatus.BAD_REQUEST);
        }
        List<Book> bookList= libraryManagementService.searchBook(request);
        if (ObjectUtils.isEmpty(bookList)) {
            return new ResponseEntity<>(new BaseResponse("No Book found",null),HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(new BaseResponse("book list successfully fetched",bookList),HttpStatus.OK);
    }

    @PostMapping("/addBook-inWishList/{isbn}/{userId}")
    public ResponseEntity<BaseResponse> addBookInWishList(@PathVariable String isbn, @PathVariable String userId) throws Exception {

        if (StringUtil.isNullOrEmpty(isbn) || StringUtil.isNullOrEmpty(userId)) {
            return new ResponseEntity<>(new BaseResponse("Invalid parameters", null), HttpStatus.BAD_REQUEST);
        }
        Book book = libraryManagementService.addInWishList(isbn, userId);
        return new ResponseEntity<>(new BaseResponse("book Details updated successfully",book),HttpStatus.OK);
    }
}
