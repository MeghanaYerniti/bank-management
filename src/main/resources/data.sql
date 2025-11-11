INSERT INTO customer_entity (name, pan, email, phone, created_at, updated_at) VALUES
('John Doe', 'ABCDE1234F', 'john.doe@example.com', '+919876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'FGHIJ5678K', 'jane.smith@example.com', '9876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO bank_account (customer_id, account_holder_name, type, balance, interest_rate, account_status, last_transaction_timestamp, created_at, updated_at) VALUES
(1, 'John Doe', 'SAVINGS', 1000.50, 3.5, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'John Doe', 'CURRENT', 5000.00, 0.0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Jane Smith', 'SAVINGS', 2500.75, 3.5, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Jane Smith', 'CURRENT', 750.25, 0.0, 'CLOSED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO transactions (transaction_id, from_account_id, to_account_id, amount, initial_balance, remaining_balance, transaction_type, timestamp, status) VALUES
('12c427e2-2774-4fdc-b411-952ee8114d3a', NULL, 10000000, 1000.00, 0.00, 1000.50, 'DEPOSIT', CURRENT_TIMESTAMP, 'SUCCESS'),
('827e6967-bb93-46bd-9dc5-29d46cf82d1c', 10000000, NULL, 200.00, 1000.50, 800.50, 'WITHDRAW', CURRENT_TIMESTAMP, 'SUCCESS'),
('daaa412e-87fe-4a71-945c-1e3f5874ca98', 10000000, 10000002, 300.00, 800.50, 500.50, 'TRANSFER', CURRENT_TIMESTAMP, 'SUCCESS');
