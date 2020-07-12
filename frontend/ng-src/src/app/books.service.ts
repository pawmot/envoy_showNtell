import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Book, BookDetails} from './model';

@Injectable({
  providedIn: 'root'
})
export class BooksService {

  constructor(private httpClient: HttpClient) { }

  getAllBooks(): Promise<Book[]> {
    return this.httpClient.get<Book[]>("/api/books", {observe: 'response', headers: {
        "X-b3-sampled": "1"
      }})
      .toPromise()
      .then(res => {
        if (res.ok) {
          return res.body;
        } else {
          throw res.statusText;
        }
      });
  }

  getBookDetails(id: string): Promise<BookDetails> {
    return this.httpClient.get<BookDetails>(`/api/books/${id}`, { observe: 'response', headers: {
        "X-b3-sampled": "1"
      }})
      .toPromise()
      .then(res => {
        if (res.ok) {
          return res.body;
        } else {
          throw res.statusText;
        }
      })
  }
}
