-- movies.dat
CREATE TABLE movies (
  movieID                 VARCHAR(10),
  title                   VARCHAR(128),
  imdbID                  VARCHAR(10),
  snapishTitle            VARCHAR(128),
  imdbPictureURL          VARCHAR(2083),
  year                    VARCHAR(5),
  rtID                    VARCHAR(128),
  rtAllCriticsRating      NUMBER,
  rtAllCriticsNumReviews  NUMBER,
  rtAllCriticsNumFresh    NUMBER,
  rtAllCriticsNumRotten   NUMBER,
  rtAllCriticsScore       NUMBER,
  rtTopCriticsRating      NUMBER,
  rtTopCriticsNumReviews  NUMBER,
  rtTopCriticsNumFresh    NUMBER,
  rtTopCriticsNumRotten   NUMBER,
  rtTopCriticsScore       NUMBER,
  rtAudienceRating        NUMBER,
  rtAudienceNumRatings    NUMBER,
  rtAudienceScore         NUMBER,
  rtPictureURL            VARCHAR(2083),
  PRIMARY KEY (movieID)
);
-- movie_genres.dat
CREATE TABLE movie_genres (
  movieID  VARCHAR(10),
  genre    VARCHAR(20),
  PRIMARY KEY (movieID, genre),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);
-- movie_countries.dat
CREATE TABLE movie_countries (
  movieID  VARCHAR(10),
  country  VARCHAR(30),
  PRIMARY KEY (movieID, country),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);
-- movie_locations.dat
CREATE TABLE movie_locations (
  movieID   VARCHAR(10),
  location1 VARCHAR(50),
  location2 VARCHAR(50),
  location3 VARCHAR(70),
  location4 VARCHAR(150),
  PRIMARY KEY (movieID, location1, location2, location3, location4),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);

-- tags.dat
CREATE TABLE tags (
  tagID  VARCHAR(10),
  value  VARCHAR(50),
  PRIMARY KEY (tagID)
);
-- movie_tags.dat
CREATE TABLE movie_tags (
  movieID   VARCHAR(10),
  tagID     VARCHAR(10),
  tagWeight VARCHAR(5),
  PRIMARY KEY (movieID, tagID),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE,
  FOREIGN KEY (tagID) REFERENCES tags(tagID) ON DELETE CASCADE
);

-- user_ratedmovies.dat + user_ratedmovies-timestamps.dat
-- timestamp format (yyyy/mm/dd hh:mm:ss)
CREATE TABLE user_ratedmovies (
  userID   VARCHAR(10),
  movieID  VARCHAR(10),
  rating   NUMBER,
  times    TIMESTAMP(5),
  PRIMARY KEY (userID, movieID),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);

-- redundant tables
-- user_taggedmovies.dat + user_taggedmovies-timestamps.dat
-- timestamp format (yyyy/mm/dd hh:mm:ss)
CREATE TABLE user_taggedmovies (
  userID   VARCHAR(10),
  movieID  VARCHAR(10),
  tagID    VARCHAR(10),
  times    TIMESTAMP(5),
  PRIMARY KEY (userID, movieID, tagID),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE,
  FOREIGN KEY (tagID) REFERENCES tags(tagID) ON DELETE CASCADE
);
-- movie_actors.dat
CREATE TABLE movie_actors (
  movieID    VARCHAR(10),
  actorID    VARCHAR(50),
  actorName  VARCHAR(50),
  ranking    NUMBER,
  PRIMARY KEY (movieID, actorID),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);
-- movie_directors.dat
CREATE TABLE movie_directors (
  movieID       VARCHAR(10),
  directorID    VARCHAR(50),
  directorName  VARCHAR(50),
  PRIMARY KEY (movieID, directorID),
  FOREIGN KEY (movieID) REFERENCES movies(movieID) ON DELETE CASCADE
);

