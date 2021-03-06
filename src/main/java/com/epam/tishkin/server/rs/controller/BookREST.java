package com.epam.tishkin.server.rs.controller;

import com.epam.tishkin.models.Book;
import com.epam.tishkin.models.BooksList;
import com.epam.tishkin.server.rs.config.HistoryManager;
import com.epam.tishkin.server.rs.config.TokenManager;
import com.epam.tishkin.server.rs.filter.UserAuth;
import com.epam.tishkin.server.rs.service.LibraryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.*;
import java.util.List;

@Path("/books")
public class BookREST {
    final static Logger logger = LogManager.getLogger(BookREST.class);
    final TokenManager tokenManager = new TokenManager();

    @Inject
    private LibraryService libraryService;

    @UserAuth
    @POST
    @Path("/add")
    public Response addNewBook(
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt,
            Book book) {
        if (libraryService.addNewBook(book)) {
            String login = tokenManager.getLoginFromJWT(jwt);
            HistoryManager.write(login, "New book added: " + book.getTitle());
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @DELETE
    @Path("/delete/{authorName}/{bookTitle}")
    public Response deleteBook(
            @PathParam("authorName") String authorName,
            @PathParam("bookTitle") String bookTitle,
            @CookieParam(TokenManager.AUTHORIZATION_PROPERTY) String jwt) {
        if (libraryService.deleteBook(authorName, bookTitle)) {
            String login = tokenManager.getLoginFromJWT(jwt);
            HistoryManager.write(login, "book deleted: " + bookTitle);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @UserAuth
    @POST
    @Path("/add-from-catalog")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBooksFromCatalog(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        File file = new File("src/main/resources/" + fileMetaData.getFileName());
        try (OutputStream out = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        int booksAdded = libraryService.addBooksFromCatalog(file);
        boolean isFileDeleted = file.delete();
        if (!isFileDeleted) {
            logger.info(file.getName() + " not found");
        }
        return Response.status(200).entity(Integer.toString(booksAdded)).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-title/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByTitle(@PathParam("title") String title) {
        BooksList booksList = new BooksList();
        List<Book> foundBooks = libraryService.getBooksByTitle(title);
        booksList.setBooks(foundBooks);
        return Response.status(200).entity(booksList).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-author/{bookAuthor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByAuthor(@PathParam("bookAuthor") String authorName) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.getBooksByAuthor(authorName);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-isbn/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksForISBN(@PathParam("isbn") String isbn) {
        Book book = libraryService.getBookByISBN(isbn);
        return Response.status(200).entity(book).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-years/{startYear}/{finishYear}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByYearRange(
            @PathParam("startYear") Integer startYear,
            @PathParam("finishYear") Integer finishYear) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.getBooksByYearRange(startYear, finishYear);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-year-pages-title/{year}/{pages}/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByYearPagesNumberAndTitle(
            @PathParam("year") Integer year,
            @PathParam("pages") Integer pages,
            @PathParam("title") String title) {
        BooksList list = new BooksList();
        List<Book> findBooks = libraryService.getBooksByYearPagesNumberAndTitle(year, pages, title);
        list.setBooks(findBooks);
        return Response.status(200).entity(list).build();
    }

    @UserAuth
    @GET
    @Path("/get-by-full-title/{bookTitle}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByFullTitle(@PathParam("bookTitle") String bookTitle) {
        Book book = libraryService.getBookByFullTitle(bookTitle);
        return Response.status(200).entity(book).build();
    }
}
