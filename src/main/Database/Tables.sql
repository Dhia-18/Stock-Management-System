-- The full code to create the database...

CREATE TYPE product_category AS ENUM('Consumable','Non-Consumable');
CREATE TYPE services AS ENUM('Bibliothèque', 'Informatique', 'Enseignement', 'Ressources Humaines', 'Nettoyage', 'Médicale', 'Autres');
CREATE TYPE order_status AS ENUM('Pending', 'Shipped', 'Cancelled');

CREATE TABLE admin(
	username VARCHAR(30),
	first_name VARCHAR(15),
	last_name VARCHAR(15),
	email VARCHAR(30),
	password VARCHAR(20),
	avatarPath VARCHAR(100),

	PRIMARY KEY (username)
);

CREATE TABLE products(
	ref SERIAL,
	name VARCHAR(30) NOT NULL,
	category product_category,
	criticism BOOLEAN DEFAULT FALSE,

	PRIMARY KEY (ref)
);

CREATE TABLE suppliers(
	id SERIAL,
	name VARCHAR(30) UNIQUE NOT NULL,
	address VARCHAR(30),
	email VARCHAR(30) UNIQUE,
	phone VARCHAR(15) UNIQUE,

	PRIMARY KEY (id)
);

CREATE TABLE warehouses(
	id SERIAL,
	name VARCHAR(15) NOT NULL,
	service services,

	PRIMARY KEY (id)
);

CREATE TABLE customers(
	id SERIAL,
	name VARCHAR(20) NOT NULL,
	service services,
	email VARCHAR(40) UNIQUE,

	PRIMARY KEY (id)
);

CREATE TABLE orders(
	id SERIAL,
	ref INTEGER NOT NULL,
	customer_id INTEGER NOT NULL,
	deliver_date DATE,
	warehouse_from_id INTEGER NOT NULL,
	warehouse_to_id INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	status order_status,

	PRIMARY KEY(id),
	FOREIGN KEY (ref) REFERENCES products(ref),
	FOREIGN KEY (customer_id) REFERENCES customers(id),
	FOREIGN KEY (warehouse_from_id) REFERENCES warehouses(id),
	FOREIGN KEY (warehouse_to_id) REFERENCES warehouses(id)
);

CREATE TABLE inventory_products(
	id SERIAL,
	ref INTEGER NOT NULL,
	expiration_date DATE DEFAULT NULL,
	supplier_id INTEGER,
	deliver_date DATE,
	warehouse_id INTEGER NOT NULL,
	quantity INTEGER,

	PRIMARY KEY (id),
	FOREIGN KEY (ref) REFERENCES products(ref),
	FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
	FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

-- Sequences..
CREATE SEQUENCE customers_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;
CREATE SEQUENCE inventory_products_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;
CREATE SEQUENCE orders_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;
CREATE SEQUENCE products_ref_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;
CREATE SEQUENCE suppliers_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;
CREATE SEQUENCE warehouses_id_seq START WITH 1 INCREMENT BY 1 MINVALUE 1;


-- Some data to start with...
	-- Inserting admin Data (REQUIRED!):
	INSERT INTO Admin Values ('admin', 'Nathan', 'Kusber', 'Nathan.Kusber@gmail.com', 'admin', 'src/ressources/Images/User-Logo.png');
	-- Inserting some suppliers:
	INSERT INTO suppliers (name, address, email, phone) VALUES ('Alpha Supplies', '123 Main St', 'contact@alpha.com', '1234567890');
	INSERT INTO suppliers (name, address, email, phone) VALUES ('TechZone', '456 Tech Ave', 'info@techzone.com', '2345678901');
	INSERT INTO suppliers (name, address, email, phone) VALUES ('OfficePlus', '789', 'sales@officeplus.com', '3456789012');
	INSERT INTO suppliers (name, address, email, phone) VALUES ('GigaStore', '22 Hardware Blvd', 'support@gigastore.com', '4567890123');
	INSERT INTO suppliers (name, address, email, phone) VALUES ('SmartLogistics', '55 Transport Ln', 'admin@smartlog.com', '5678901234');
	-- Inserting some warehouses:
	INSERT INTO warehouses (name, service) VALUES ('Bib-1', 'Bibliothèque');
	INSERT INTO warehouses (name, service) VALUES ('Amphi-A', 'Enseignement');
	INSERT INTO warehouses (name, service) VALUES ('A-11', 'Enseignement');
	INSERT INTO warehouses (name, service) VALUES ('Bureau-Admin-1', 'Administration');
	INSERT INTO warehouses (name, service) VALUES ('Magasin', 'Magasin');
	INSERT INTO warehouses (name, service) VALUES ('Bureau-Med-1', 'Médicale');
	-- Inserting some customers:
	INSERT INTO customers (name, service, email) VALUES ('Amira Ben Ahmed', 'Enseignement', 'amira@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Youssef Trabelsi', 'Informatique', 'youssef@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Leïla Gharbi', 'Bibliothèque', 'leila@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Hichem Bouazizi', 'Médicale', 'hichem@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Sarra Khemiri', 'Autres', 'sarra@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Anis Mbarek', 'Magasin', 'anis@fac.tn');
	INSERT INTO customers (name, service, email) VALUES ('Mohamed Graiet', 'Administration', 'mohamed@fac.tn');
