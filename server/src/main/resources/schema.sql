DROP TABLE IF EXISTS public.bookings CASCADE;
DROP TABLE IF EXISTS public.items CASCADE;
DROP TABLE IF EXISTS public.requests CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.comments CASCADE;

CREATE TABLE users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_time TIMESTAMP NOT NULL,
    description VARCHAR(255) NOT NULL,
    requestor_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    available BOOLEAN,
    description VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT REFERENCES users(user_id),
    request_id BIGINT REFERENCES requests(request_id)
);

CREATE TABLE bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    end_time TIMESTAMP,
    start_time TIMESTAMP,
    status VARCHAR(255) NOT NULL,
    booker_id BIGINT REFERENCES users(user_id),
    item_id BIGINT REFERENCES items(item_id)
);

CREATE TABLE comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_time TIMESTAMP,
    text VARCHAR(255),
    author_id BIGINT REFERENCES users(user_id),
    item_id BIGINT REFERENCES items(item_id)
);