import {Component, OnInit} from '@angular/core';
import {BooksService} from './books.service';
import {Book, BookDetails} from './model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'envoy-snt';

  selectedBook: Book = null;
  selectedBookDetails: BookDetails = null;
  books: Book[];

  constructor(private service: BooksService) {
  }

  async ngOnInit(): Promise<void> {
    this.books = await this.service.getAllBooks();
  }

  async select(book: Book) {
    this.selectedBook = book;
    this.selectedBookDetails = await this.service.getBookDetails(book.id);
  }
}
