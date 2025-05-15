-- Categories
INSERT INTO category (name) VALUES
    ('Electronics'),
    ('Vehicles'),
    ('Books'),
    ('Fashion'),
    ('Home Appliances');


-- Users
INSERT INTO users (username, password, email, role, balance) VALUES
    ('grace', 'pass7', 'grace@example.com', 'ADMIN', null),
    ( 'dave', 'pass4', 'dave@example.com', 'ADMIN', null),
    ('alice', 'pass1', 'alice@example.com', 'USER', 500.00),
    ('bob', 'pass2', 'bob@example.com', 'USER', 800.00),
    ( 'carol', 'pass3', 'carol@example.com', 'MODERATOR', null),
    ( 'eve', 'pass5', 'eve@example.com', 'USER', 300.00),
    ('frank', 'pass6', 'frank@example.com', 'MODERATOR', null);

-- Auctions
INSERT INTO auction (
    title, description, starting_price, start_time, end_time,
    auction_status, actual_price, watcher_count, user_id, category_id
) VALUES
      ('iPhone 14', 'Brand new iPhone 14', 300.00, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', 320.00, 4, 1, 1),
      ('Samsung Galaxy S22', 'Brand new Samsung Galaxy S22', 700.00, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', 720.00, 5, 3, 1),
      ('MacBook Pro 13"', 'Apple MacBook Pro 13-inch, M1 chip', 1200.00, CURRENT_TIMESTAMP, DATEADD('DAY', 10, CURRENT_TIMESTAMP), 'ACTIVE', 1250.00, 7, 4, 1),
      ('Tesla Model S', 'Used Tesla, great condition', 25000.00, CURRENT_TIMESTAMP, DATEADD('DAY', 14, CURRENT_TIMESTAMP), 'ACTIVE', 25500.00, 6, 2, 2),
      ('Harry Potter Collection', 'All 7 books in English', 50.00, CURRENT_TIMESTAMP, DATEADD('DAY', 5, CURRENT_TIMESTAMP), 'ACTIVE', 55.00, 2, 3, 3),
      ('Leather Jacket', 'Men’s black leather jacket, size L', 80.00, CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_TIMESTAMP), 'ACTIVE', 90.00, 1, 1, 4),
      ( 'Dyson Vacuum Cleaner', 'Like new, barely used', 200.00, CURRENT_TIMESTAMP, DATEADD('DAY', 10, CURRENT_TIMESTAMP), 'ACTIVE', 210.00, 3, 5, 5);

-- Bids
INSERT INTO bid (amount, bit_time, user_id, auction_id) VALUES
    (320.00, CURRENT_TIMESTAMP, 3, 1),
    (25500.00, CURRENT_TIMESTAMP, 4, 2),
    (55.00, CURRENT_TIMESTAMP, 5, 3),
    (90.00, CURRENT_TIMESTAMP, 2, 4),
    (210.00, CURRENT_TIMESTAMP, 1, 5);

-- Auction images
INSERT INTO auction_image (url) VALUES
    ( 'http://example.com/images/iphone14.jpg'),
    ('http://example.com/images/tesla.jpg'),
    ('http://example.com/images/harrypotter.jpg'),
    ('http://example.com/images/jacket.jpg'),
    ('http://example.com/images/dyson.jpg');
