# fin-data

This application will parse the Official Cash Rate from the Reserve Bank of New Zealand web site.
It saves the rate, announcement date, and link to the announcement in a PostgresQL database.
It serves historical OCR changes on request by a GraphQL query.

This application assumes 9:00 AM NZ local time on the announcement date.
In the query results the announced_on field is an ISO8601 combined UTC date and time.

## Installation



## Usage

    $ createdb fin-data           # create the postgresql database
    $ lein run -m fin-data.db     # create database table and populate with data
    $ lein ring server-headless   # runs the http server

In a browser location bar, try it out with

    http://localhost:3000/graphql?query={official_cash_rates{announced_on+rate+link}}

Or using curl

    curl -X POST -H "Content-Type:application/json" -d '
      {
        "query":"{official_cash_rates { announced_on rate link }}"
      } ' http://localhost:3000/graphql

Example response,

    {
    "data": {
      "official_cash_rates": [
        {
          "announced_on": "2017-03-22T20:00:00Z",
          "rate": 1.75,
          "link": "http://www.rbnz.govt.nz/news/2017/03/official-cash-rate-unchanged-at-1-75-percent"
        },
        {
          "announced_on": "2017-02-08T20:00:00Z",
          "rate": 1.75,
          "link": "http://www.rbnz.govt.nz/news/2017/02/official-cash-rate-unchanged-at-1-75-percent"
        },
        {
          "announced_on": "2016-11-09T20:00:00Z",
          "rate": 1.75,
          "link": "http://www.rbnz.govt.nz/news/2016/11/official-cash-rate-reduced-to-1-75-percent"
        },
      }
    }


## License

Copyright © 2017 A. Carl Douglas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
