const PROXY_CONFIG = {
  '/api/books': {
    'bypass': (req, res) => {
      if (req.originalUrl === '/api/books') {
        console.log("Got /api/books request");
        const result = [{
          "id": "4857cbaa-4894-437e-9df6-f692d91eda72",
          "title": "C# in Depth"
        }];

        res.end(JSON.stringify(result));
        return true;
      } else if (req.originalUrl === '/api/books/4857cbaa-4894-437e-9df6-f692d91eda72') {
        console.log("Got /api/books/4857cbaa-4894-437e-9df6-f692d91eda72 request");

        const result = {
          "details": {
            "id": "4857cbaa-4894-437e-9df6-f692d91eda72",
            "title": "C# in Depth",
            "author": "Jon Skeet",
            "description": "C# in Depth is a 2008 book by Jon Skeet which aims to help C# developers master the language. The second edition was published in 2010. The third edition was published in 2013. A fourth edition, due for release in 2018, was published by Skeet on March 23rd, 2019."
          },
          "reviews": [
            {
              "bookId": "4857cbaa-4894-437e-9df6-f692d91eda72",
              "id": "f6d51ae7-e03c-4fd7-9caf-b6421b973b10",
              "text": "Great book, would recommend!",
              "rating": 5
            },
            {
              "bookId": "4857cbaa-4894-437e-9df6-f692d91eda72",
              "id": "9cb99d86-2c3a-486e-8a9d-0eb98204d158",
              "text": "Very nice, but has some cheap jokes",
              "rating": 4
            },
            {
              "bookId": "4857cbaa-4894-437e-9df6-f692d91eda72",
              "id": "c142a3ee-5a94-43d5-bb8c-c0dd8252e119",
              "text": "I could not understand a word",
              "rating": 1
            }
          ],
          nonFatalErrors: {}
        };

        let ok = Math.random() > 0.5;

        res.end(!ok ? JSON.stringify(result) : JSON.stringify({...result, reviews: [], nonFatalErrors: { reviews: "ERR" }}));
        return true;
      }
    }
  }
}

module.exports = PROXY_CONFIG;
