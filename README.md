# Spark Webcrawler
This Java application implements a web crawler using the Spark framework. The web crawler is designed to search for specific terms on web pages.

### Execução

1. Clone the repository
   ``` bash
   git clone git@github.com:vhsilvat/sparkcrawler.git
   ```
2. Navigate to the project directory
   ``` bash
   cd sparkcrawler
   ```
3. Compile and create the Docker image
   ``` bash
   docker build . -t sparkcrawler
   ```
4. Start the Docker container
   ``` bash
   docker run -e BASE_URL=http://sua-url-aqui.com -p 4567:4567 --rm sparkcrawler
   ```
5. The application will be accessible at http://localhost:4567.

## API Endpoints
1. **GET /crawl/{id}**: Get results from a specific search by ID.
   ``` http
   GET /crawl/abc123
   ```
2. **POST /crawl**: Initiate a new search. Send a JSON with the key "keyword" containing the term to be searched.
   ``` http
   POST /crawl

   {
     "keyword": "search-term"
   }
   ```
