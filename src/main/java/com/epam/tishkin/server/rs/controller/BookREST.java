package com.epam.tishkin.server.rs.controller;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.BooksList;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.List;

@Path("/books")
public class BookREST {

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addNewBook(Book book) {
        if (libraryService.addNewBook(book)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{authorName}/{bookTitle}")
    public Response deleteBook(
            @PathParam("authorName") String authorName,
            @PathParam("bookTitle") String bookTitle) {
        if (libraryService.deleteBook(authorName, bookTitle)) {
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @POST
    @Path("/add-from-catalog")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBooksFromCatalog(File file) {
        System.out.println(file);
        System.out.println(file.getName());
        int booksAdded = libraryService.addBooksFromCatalog(file);
        return Response.status(200).entity(booksAdded).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-title/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBookForTitle(@PathParam("title") String title) {
        BooksList booksList = new BooksList();
        List<Book> foundBooks = libraryService.searchBookForTitle(title);
        booksList.setBooks(foundBooks);
        return Response.status(200).entity(booksList).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-author/{bookAuthor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooksForAuthor(@PathParam("bookAuthor") String authorName) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBooksForAuthor(authorName);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-isbn/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBookForISBN(@PathParam("isbn") String isbn) {
        Book book = libraryService.searchBookForISBN(isbn);
        return Response.status(200).entity(book).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-years/{startYear}/{finishYear}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooksByYearRange(
            @PathParam("startYear") Integer startYear,
            @PathParam("finishYear") Integer finishYear) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBooksByYearRange(startYear, finishYear);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-for-year-pages-title/{year}/{pages}/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBookByYearPagesNumberAndTitle(
            @PathParam("year") Integer year,
            @PathParam("pages") Integer pages,
            @PathParam("title") String title) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.searchBookByYearPagesNumberAndTitle(year, pages, title);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/search-by-full-title/{bookTitle}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBookByFullTitle(@PathParam("bookTitle") String bookTitle) {
        Book book = libraryService.findBookByFullTitle(bookTitle);
        return Response.status(200).entity(book).build();
    }
}