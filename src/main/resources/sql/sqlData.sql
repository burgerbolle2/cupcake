-- Data for cupcakes for database

BEGIN;

-- Inserting data into the bottom table
INSERT INTO public.bottom (name, price)
VALUES
    ('Chocolate', 5.00),
    ('Vanilla', 5.00),
    ('Nutmeg', 5.00),
    ('Pistacio', 6.00),
    ('Almond', 7.00);

-- Inserting data into the top table
INSERT INTO public.top (name, price)
VALUES
    ('Chocolate', 5.00),
    ('Blueberry', 5.00),
    ('Raspberry', 5.00),
    ('Crispy', 6.00),
    ('Strawberry', 6.00),
    ('Rum/Raisin', 7.00),
    ('Orange', 8.00),
    ('Lemon', 8.00),
    ('Blue cheese', 9.00);

COMMIT;
