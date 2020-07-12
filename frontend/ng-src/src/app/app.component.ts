import {Component, OnInit} from '@angular/core';
import {BooksService} from './books.service';
import {Book, BookDetails} from './model';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ProgressSpinnerDialogComponent} from './progress-spinner-dialog/progress-spinner-dialog.component';

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
  loadingCtr = 0;
  overlayRef: MatDialogRef<ProgressSpinnerDialogComponent> = null;
  errorText = "";

  constructor(private service: BooksService, private dialog: MatDialog) {
  }

  async ngOnInit(): Promise<void> {
    this.books = await this.service.getAllBooks();
  }

  async select(book: Book) {
    if (this.overlayRef === null) {
      this.overlayRef = this.dialog.open(ProgressSpinnerDialogComponent, {
        panelClass: 'transparent',
        disableClose: true
      });
    }
    this.loadingCtr++;
    this.selectedBook = book;
    let selectedBookDetails: BookDetails;
    let errorText = "";
    try {
      selectedBookDetails = await this.service.getBookDetails(book.id);
    } catch (e) {
      errorText = e.toString();
    } finally {
      this.loadingCtr--;
      this.errorText = errorText;

      if (this.loadingCtr === 0) {
        this.overlayRef.close();
        this.overlayRef = null;
        if (selectedBookDetails) {
          this.selectedBookDetails = selectedBookDetails;
        }
      }
    }
  }
}
