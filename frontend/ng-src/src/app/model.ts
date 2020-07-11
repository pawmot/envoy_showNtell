export interface Book {
  id: string;
  title: string;
}

export interface BookDetails {
  details: {
    id: string;
    title: string;
    author: string;
    description: string;
  };

  reviews: [{
    bookId: string;
    id: string;
    text: string;
    rating: number;
  }];
}
