-- Categories
INSERT INTO category (name) VALUES
    ('Electronics'),
    ('Vehicles'),
    ('Books'),
    ('Fashion'),
    ('Home Appliances');


-- Users
--passwords: pass1, pass2, pass3 ...
INSERT INTO users (username, password, email, role, balance) VALUES
    ('grace', '$2a$12$0RCZZzyCU3DbfL3t91g.OOYwhGZnJp1xuJP3oliJ7BqrvJbrQeA/m', 'grace@example.com', 'ADMIN', null),
    ( 'dave', '$2a$12$xGXT1mCPttP0DfFndlLaSeAaj4ktZzLoxPXpBRpKdkiAbTaP5IJbW', 'dave@example.com', 'USER', null),
    ('alice', '$2a$12$YhgKB0bj1rffUtfAtm4smOgKhtkL0PHp/712EBamqPMB3xbiaMrvK', 'alice@example.com', 'USER', 500.00),
    ('bob', '$2a$12$FirP/yXDI3SXaOBiQtUxLe1x4KpzreSOtOl/2i.a5BZvrGD5aYTji', 'bob@example.com', 'USER', 800.00),
    ( 'carol', '$2a$12$KEk/Y3wKfi.A19SQWry9jeUFlL4l39Iww1uJb3MEtUZImcmGksuBC', 'carol@example.com', 'MODERATOR', null),
    ( 'eve', '$2a$12$tai130KC4TB3SeP1GpUNduV/SGIc..lrNnQmzU5R06Y/sUBXO5vme', 'eve@example.com', 'USER', 300.00),
    ('frank', '$2a$12$jQdj1yyY9Ixh9PPNehZITeoYKdq35Cbg88gZiHLDTQplYtF8NcCEq', 'frank@example.com', 'MODERATOR', null);

-- Auctions
INSERT INTO auction (
    title, description, starting_price, start_time, end_time,
    status, actual_price, user_id, category_id
) VALUES
      ('iPhone 14', 'Brand new iPhone 14', 300.00, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', 320.00,  2, 1),
      ('Samsung Galaxy S22', 'Brand new Samsung Galaxy S22', 700.00, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', 720.00,  3, 1),
      ('MacBook Pro 13"', 'Apple MacBook Pro 13-inch, M1 chip', 1200.00, CURRENT_TIMESTAMP, DATEADD('DAY', 10, CURRENT_TIMESTAMP), 'ACTIVE', 1250.00,  4, 1),
      ('Tesla Model S', 'Used Tesla, great condition', 25000.00, CURRENT_TIMESTAMP, DATEADD('DAY', 14, CURRENT_TIMESTAMP), 'ACTIVE', 25500.00,  2, 2),
      ('Harry Potter Collection', 'All 7 books in English', 50.00, CURRENT_TIMESTAMP, DATEADD('DAY', 5, CURRENT_TIMESTAMP), 'ACTIVE', 55.00,  3, 3),
      ('Leather Jacket', 'Men’s black leather jacket, size L', 80.00, CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_TIMESTAMP), 'ACTIVE', 90.00,  6, 4),
      ('Dyson Vacuum Cleaner', 'Like new, barely used', 200.00, CURRENT_TIMESTAMP, DATEADD('DAY', 10, CURRENT_TIMESTAMP), 'ACTIVE', 210.00,  6, 5);

-- Bids
INSERT INTO bid (amount, bit_time, user_id, auction_id) VALUES
    (320.00, CURRENT_TIMESTAMP, 3, 1),
    (25500.00, CURRENT_TIMESTAMP, 4, 2),
    (55.00, CURRENT_TIMESTAMP, 5, 3),
    (90.00, CURRENT_TIMESTAMP, 2, 4),
    (210.00, CURRENT_TIMESTAMP, 1, 5);
