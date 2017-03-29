# fin-data

This application will parse the OCR rate from the RBNZ web site, save the rate, announcement date, and link to announcement in a PostgresQL database.
It serves the OCR rate data on request by way of a GraphQL query.

The website provides only announcement date, and this application assumes the time is assumed to be 9am NZ local time on that date.  In the query results the announced_on field is an ISO8601 combined UTC date and time.

## Installation



## Usage

    $ createdb fin-data           # create the postgresql database
    $ lein run -m fin-data.db     # create database table and populate with data
    $ lein ring server-headless   # runs the http server

In a browser,

    http://localhost:3000/graphql?query={official_cash_rates{announced_on+rate+link}}


## Options



## Examples



### Bugs



### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2017 A. Carl Douglas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
