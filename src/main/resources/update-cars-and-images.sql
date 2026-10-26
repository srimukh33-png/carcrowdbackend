-- CarCrowd Comprehensive Car and Image Update Script
START TRANSACTION;
UPDATE cars SET image_url = '/cars/audi-a4.avif' WHERE id = 115;
UPDATE cars SET image_url = '/cars/audi-a6.avif' WHERE id = 116;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 16, 'Q3 Sportback', '/cars/audi-q3-sportback.avif', 'SUV', 'Experience the Q3 Sportback by Audi. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 16 AND LOWER(model_name) = LOWER('Q3 Sportback'));
UPDATE cars SET image_url = '/cars/audi-q3.avif' WHERE id = 117;
UPDATE cars SET image_url = '/cars/audi-q5.avif' WHERE id = 118;
UPDATE cars SET image_url = '/cars/audi-q7.avif' WHERE id = 119;
UPDATE cars SET image_url = '/cars/audi-q8.avif' WHERE id = 120;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 16, 'RS Q8', '/cars/audi-rs-q8.avif', 'SUV', 'Experience the RS Q8 by Audi. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 16 AND LOWER(model_name) = LOWER('RS Q8'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 16, 'S5 Sportback', '/cars/audi-s5-sportback.avif', 'Sedan', 'Experience the S5 Sportback by Audi. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 16 AND LOWER(model_name) = LOWER('S5 Sportback'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 16, 'SQ8', '/cars/audi-sq8.avif', 'SUV', 'Experience the SQ8 by Audi. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 16 AND LOWER(model_name) = LOWER('SQ8'));
UPDATE cars SET image_url = '/cars/bmw-2-series-gran-coupe.avif' WHERE id = 95;
UPDATE cars SET image_url = '/cars/bmw-3-series.avif' WHERE id = 96;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, '3 Series Long Wheelbase', '/cars/bmw-3-series-long-wheelbase.avif', 'Sedan', 'Experience the 3 Series Long Wheelbase by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('3 Series Long Wheelbase'));
UPDATE cars SET image_url = '/cars/bmw-5-series.avif' WHERE id = 97;
UPDATE cars SET image_url = '/cars/bmw-7-series.avif' WHERE id = 98;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M2', '/cars/bmw-m2.avif', 'Crossover / SUV', 'Experience the M2 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M2'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M4 CS', '/cars/bmw-m4-cs.avif', 'Crossover / SUV', 'Experience the M4 CS by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M4 CS'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M4 Competition', '/cars/bmw-m4-competition.avif', 'Crossover / SUV', 'Experience the M4 Competition by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M4 Competition'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M440i', '/cars/bmw-m440i.avif', 'Crossover / SUV', 'Experience the M440i by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M440i'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M5', '/cars/bmw-m5.avif', 'Crossover / SUV', 'Experience the M5 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M5'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'M8 Coupe Competition', '/cars/bmw-m8-coupe-competition.avif', 'Coupe / Roadster', 'Experience the M8 Coupe Competition by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('M8 Coupe Competition'));
UPDATE cars SET image_url = '/cars/bmw-x1.avif' WHERE id = 99;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'X1 LWB', '/cars/bmw-x1-lwb.avif', 'SUV', 'Experience the X1 LWB by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('X1 LWB'));
UPDATE cars SET image_url = '/cars/bmw-x5.avif' WHERE id = 101;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'X6', '/cars/bmw-x6.avif', 'SUV', 'Experience the X6 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('X6'));
UPDATE cars SET image_url = '/cars/bmw-x7.avif' WHERE id = 102;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'XM', '/cars/bmw-xm.avif', 'SUV', 'Experience the XM by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('XM'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'Z4', '/cars/bmw-z4.avif', 'Coupe / Roadster', 'Experience the Z4 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('Z4'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'i4', '/cars/bmw-i4.avif', 'Crossover / SUV', 'Experience the i4 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('i4'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'i5', '/cars/bmw-i5.avif', 'Crossover / SUV', 'Experience the i5 by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('i5'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'i5 LWB', '/cars/bmw-i5-lwb.avif', 'Crossover / SUV', 'Experience the i5 LWB by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('i5 LWB'));
UPDATE cars SET image_url = '/cars/bmw-i7.avif' WHERE id = 104;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 14, 'iX', '/cars/bmw-ix.avif', 'SUV', 'Experience the iX by BMW. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 14 AND LOWER(model_name) = LOWER('iX'));
UPDATE cars SET image_url = '/cars/bmw-ix1.avif' WHERE id = 103;
UPDATE cars SET image_url = '/cars/byd-atto-3.avif' WHERE id = 127;
UPDATE cars SET image_url = '/cars/byd-seal.avif' WHERE id = 128;
UPDATE cars SET image_url = '/cars/byd-sealion-7.avif' WHERE id = 130;
UPDATE cars SET image_url = '/cars/byd-emax-7.avif' WHERE id = 129;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 13, 'Aircross X', '/cars/citroen-aircross-x.avif', 'Crossover / SUV', 'Experience the Aircross X by Citroen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 13 AND LOWER(model_name) = LOWER('Aircross X'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 13, 'Basalt X', '/cars/citroen-basalt-x.avif', 'Crossover / SUV', 'Experience the Basalt X by Citroen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 13 AND LOWER(model_name) = LOWER('Basalt X'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 13, 'C3 X', '/cars/citroen-c3-x.avif', 'Hatchback', 'Experience the C3 X by Citroen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 13 AND LOWER(model_name) = LOWER('C3 X'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 13, 'C5 Aircross', '/cars/citroen-c5-aircross.avif', 'Crossover / SUV', 'Experience the C5 Aircross by Citroen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 13 AND LOWER(model_name) = LOWER('C5 Aircross'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 13, 'e-C3 X', '/cars/citroen-e-c3-x.avif', 'Hatchback', 'Experience the e-C3 X by Citroen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 13 AND LOWER(model_name) = LOWER('e-C3 X'));
UPDATE cars SET image_url = '/cars/defender.avif' WHERE id = 135;
UPDATE cars SET image_url = '/cars/honda-amaze.avif' WHERE id = 67;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 7, 'Amaze 2nd Gen', '/cars/honda-amaze-2nd-gen.avif', 'Sedan', 'Experience the Amaze 2nd Gen by Honda. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 7 AND LOWER(model_name) = LOWER('Amaze 2nd Gen'));
UPDATE cars SET image_url = '/cars/honda-city.avif' WHERE id = 68;
UPDATE cars SET image_url = '/cars/honda-elevate.avif' WHERE id = 69;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 7, 'ZR-V', '/cars/honda-zr-v.avif', 'Crossover / SUV', 'Experience the ZR-V by Honda. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 7 AND LOWER(model_name) = LOWER('ZR-V'));
UPDATE cars SET image_url = '/cars/hyundai-alcazar.avif' WHERE id = 34;
UPDATE cars SET image_url = '/cars/hyundai-aura.avif' WHERE id = 30;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Bayon', '/cars/hyundai-bayon.avif', 'Crossover / SUV', 'Experience the Bayon by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Bayon'));
UPDATE cars SET image_url = '/cars/hyundai-creta.avif' WHERE id = 33;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Creta Electric', '/cars/hyundai-creta-electric.avif', 'SUV', 'Experience the Creta Electric by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Creta Electric'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Creta N Line', '/cars/hyundai-creta-n-line.avif', 'SUV', 'Experience the Creta N Line by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Creta N Line'));
UPDATE cars SET image_url = '/cars/hyundai-exter.avif' WHERE id = 31;
UPDATE cars SET image_url = '/cars/hyundai-grand-i10-nios.avif' WHERE id = 28;
UPDATE cars SET image_url = '/cars/hyundai-ioniq-5.avif' WHERE id = 37;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Prime HB', '/cars/hyundai-prime-hb.avif', 'Hatchback', 'Experience the Prime HB by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Prime HB'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Prime SD', '/cars/hyundai-prime-sd.avif', 'Crossover / SUV', 'Experience the Prime SD by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Prime SD'));
UPDATE cars SET image_url = '/cars/hyundai-venue.avif' WHERE id = 32;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'Venue N Line', '/cars/hyundai-venue-n-line.avif', 'SUV', 'Experience the Venue N Line by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('Venue N Line'));
UPDATE cars SET image_url = '/cars/hyundai-verna.avif' WHERE id = 35;
UPDATE cars SET image_url = '/cars/hyundai-i20.avif' WHERE id = 29;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 3, 'i20 N Line', '/cars/hyundai-i20-n-line.avif', 'Hatchback', 'Experience the i20 N Line by Hyundai. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 3 AND LOWER(model_name) = LOWER('i20 N Line'));
UPDATE cars SET image_url = '/cars/jeep-compass.avif' WHERE id = 131;
UPDATE cars SET image_url = '/cars/jeep-grand-cherokee.avif' WHERE id = 134;
UPDATE cars SET image_url = '/cars/jeep-meridian.avif' WHERE id = 132;
UPDATE cars SET image_url = '/cars/jeep-wrangler.avif' WHERE id = 133;
UPDATE cars SET image_url = '/cars/kia-carens.avif' WHERE id = 50;
UPDATE cars SET image_url = '/cars/kia-carens-clavis.avif' WHERE id = 51;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 5, 'Carens Clavis EV', '/cars/kia-carens-clavis-ev.avif', 'MPV', 'Experience the Carens Clavis EV by Kia. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 5 AND LOWER(model_name) = LOWER('Carens Clavis EV'));
UPDATE cars SET image_url = '/cars/kia-carnival.avif' WHERE id = 55;
UPDATE cars SET image_url = '/cars/kia-ev6.avif' WHERE id = 53;
UPDATE cars SET image_url = '/cars/kia-ev9.avif' WHERE id = 54;
UPDATE cars SET image_url = '/cars/kia-seltos.avif' WHERE id = 49;
UPDATE cars SET image_url = '/cars/kia-sonet.avif' WHERE id = 48;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 5, 'Sorento', '/cars/kia-sorento.avif', 'Crossover / SUV', 'Experience the Sorento by Kia. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 5 AND LOWER(model_name) = LOWER('Sorento'));
UPDATE cars SET image_url = '/cars/kia-syros.avif' WHERE id = 52;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 5, 'Syros EV', '/cars/kia-syros-ev.avif', 'SUV', 'Experience the Syros EV by Kia. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 5 AND LOWER(model_name) = LOWER('Syros EV'));
UPDATE cars SET image_url = '/cars/land-rover-discovery.avif' WHERE id = 137;
UPDATE cars SET image_url = '/cars/land-rover-discovery-sport.avif' WHERE id = 136;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 8, 'Astor', '/cars/mg-astor.avif', 'SUV', 'Experience the Astor by MG Motor. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 8 AND LOWER(model_name) = LOWER('Astor'));
UPDATE cars SET image_url = '/cars/mg-comet-ev.avif' WHERE id = 70;
UPDATE cars SET image_url = '/cars/mg-cyberster.avif' WHERE id = 76;
UPDATE cars SET image_url = '/cars/mg-hector.avif' WHERE id = 73;
UPDATE cars SET image_url = '/cars/mg-hector-plus.avif' WHERE id = 74;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 8, 'Hector Tomahawk EV', '/cars/mg-hector-tomahawk-ev.avif', 'SUV', 'Experience the Hector Tomahawk EV by MG Motor. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 8 AND LOWER(model_name) = LOWER('Hector Tomahawk EV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 8, 'Hector Tomahawk PHEV', '/cars/mg-hector-tomahawk-phev.avif', 'SUV', 'Experience the Hector Tomahawk PHEV by MG Motor. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 8 AND LOWER(model_name) = LOWER('Hector Tomahawk PHEV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 8, 'M9', '/cars/mg-m9.avif', 'MPV', 'Experience the M9 by MG Motor. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 8 AND LOWER(model_name) = LOWER('M9'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 8, 'Majestor', '/cars/mg-majestor.avif', 'Crossover / SUV', 'Experience the Majestor by MG Motor. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 8 AND LOWER(model_name) = LOWER('Majestor'));
UPDATE cars SET image_url = '/cars/mg-windsor-ev.avif' WHERE id = 71;
UPDATE cars SET image_url = '/cars/mg-zs-ev.avif' WHERE id = 72;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'BE 6 SPORTEQ', '/cars/mahindra-be-6-sporteq.avif', 'Crossover / SUV', 'Experience the BE 6 SPORTEQ by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('BE 6 SPORTEQ'));
UPDATE cars SET image_url = '/cars/mahindra-bolero.avif' WHERE id = 39;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'Bolero Camper', '/cars/mahindra-bolero-camper.avif', 'SUV', 'Experience the Bolero Camper by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('Bolero Camper'));
UPDATE cars SET image_url = '/cars/mahindra-bolero-neo.avif' WHERE id = 40;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'Bolero Neo Plus', '/cars/mahindra-bolero-neo-plus.avif', 'SUV', 'Experience the Bolero Neo Plus by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('Bolero Neo Plus'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'Bolero Pik-Up', '/cars/mahindra-bolero-pik-up.avif', 'SUV', 'Experience the Bolero Pik-Up by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('Bolero Pik-Up'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'Scorpio', '/cars/mahindra-scorpio.avif', 'SUV', 'Experience the Scorpio by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('Scorpio'));
UPDATE cars SET image_url = '/cars/mahindra-scorpio-n.avif' WHERE id = 44;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'Thar OG', '/cars/mahindra-thar-og.avif', 'SUV', 'Experience the Thar OG by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('Thar OG'));
UPDATE cars SET image_url = '/cars/mahindra-thar-roxx.avif' WHERE id = 42;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'XEV 9S', '/cars/mahindra-xev-9s.avif', 'Crossover / SUV', 'Experience the XEV 9S by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('XEV 9S'));
UPDATE cars SET image_url = '/cars/mahindra-xev-9e.avif' WHERE id = 46;
UPDATE cars SET image_url = '/cars/mahindra-xuv-3xo.avif' WHERE id = 38;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'XUV 3XO EV', '/cars/mahindra-xuv-3xo-ev.avif', 'Crossover / SUV', 'Experience the XUV 3XO EV by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('XUV 3XO EV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'XUV 7XO', '/cars/mahindra-xuv-7xo.avif', 'Crossover / SUV', 'Experience the XUV 7XO by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('XUV 7XO'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 4, 'XUV400 EV', '/cars/mahindra-xuv400-ev.avif', 'Crossover / SUV', 'Experience the XUV400 EV by Mahindra. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 4 AND LOWER(model_name) = LOWER('XUV400 EV'));
UPDATE cars SET image_url = '/cars/maruti-suzuki-alto-k10.avif' WHERE id = 1;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Alto Tour H1', '/cars/maruti-suzuki-alto-tour-h1.avif', 'Hatchback', 'Experience the Alto Tour H1 by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '2/5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Alto Tour H1'));
UPDATE cars SET image_url = '/cars/maruti-suzuki-baleno.avif' WHERE id = 6;
UPDATE cars SET image_url = '/cars/maruti-suzuki-brezza.avif' WHERE id = 9;
UPDATE cars SET image_url = '/cars/maruti-suzuki-celerio.avif' WHERE id = 4;
UPDATE cars SET image_url = '/cars/maruti-suzuki-dzire.avif' WHERE id = 7;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Dzire Tour S', '/cars/maruti-suzuki-dzire-tour-s.avif', 'Sedan', 'Experience the Dzire Tour S by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '2/5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Dzire Tour S'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Eeco', '/cars/maruti-suzuki-eeco.avif', 'MPV', 'Experience the Eeco by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Eeco'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Eeco Cargo', '/cars/maruti-suzuki-eeco-cargo.avif', 'MPV', 'Experience the Eeco Cargo by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Eeco Cargo'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Eeco Tour V', '/cars/maruti-suzuki-eeco-tour-v.avif', 'MPV', 'Experience the Eeco Tour V by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Eeco Tour V'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Ertiga Tour', '/cars/maruti-suzuki-ertiga-tour.avif', 'MPV', 'Experience the Ertiga Tour by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Ertiga Tour'));
UPDATE cars SET image_url = '/cars/maruti-suzuki-ertiga.avif' WHERE id = 10;
UPDATE cars SET image_url = '/cars/maruti-suzuki-fronx.avif' WHERE id = 8;
UPDATE cars SET image_url = '/cars/maruti-suzuki-grand-vitara.avif' WHERE id = 11;
UPDATE cars SET image_url = '/cars/maruti-suzuki-invicto.avif' WHERE id = 14;
UPDATE cars SET image_url = '/cars/maruti-suzuki-jimny.avif' WHERE id = 13;
UPDATE cars SET image_url = '/cars/maruti-suzuki-s-presso.avif' WHERE id = 2;
UPDATE cars SET image_url = '/cars/maruti-suzuki-swift.avif' WHERE id = 5;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Victoris', '/cars/maruti-suzuki-victoris.avif', 'Crossover / SUV', 'Experience the Victoris by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Victoris'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 1, 'Wagon R tour', '/cars/maruti-suzuki-wagon-r-tour.avif', 'Hatchback', 'Experience the Wagon R tour by Maruti Suzuki. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '2/5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 1 AND LOWER(model_name) = LOWER('Wagon R tour'));
UPDATE cars SET image_url = '/cars/maruti-suzuki-wagon-r.avif' WHERE id = 3;
UPDATE cars SET image_url = '/cars/maruti-suzuki-xl6.avif' WHERE id = 12;
UPDATE cars SET image_url = '/cars/maruti-suzuki-e-vitara.avif' WHERE id = 15;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG A 45 S', '/cars/mercedes-benz-amg-a-45-s.avif', 'Crossover / SUV', 'Experience the AMG A 45 S by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG A 45 S'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG C 63', '/cars/mercedes-benz-amg-c-63.avif', 'Sedan', 'Experience the AMG C 63 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG C 63'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG C43', '/cars/mercedes-benz-amg-c43.avif', 'Sedan', 'Experience the AMG C43 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG C43'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG CLE 53', '/cars/mercedes-benz-amg-cle-53.avif', 'Sedan', 'Experience the AMG CLE 53 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG CLE 53'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG E 53', '/cars/mercedes-benz-amg-e-53.avif', 'Sedan', 'Experience the AMG E 53 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG E 53'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG EQS', '/cars/mercedes-benz-amg-eqs.avif', 'Sedan', 'Experience the AMG EQS by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG EQS'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG GLC 43', '/cars/mercedes-benz-amg-glc-43.avif', 'SUV', 'Experience the AMG GLC 43 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG GLC 43'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG GLE 53', '/cars/mercedes-benz-amg-gle-53.avif', 'SUV', 'Experience the AMG GLE 53 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG GLE 53'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG GT 4 Door Coupe', '/cars/mercedes-benz-amg-gt-4-door-coupe.avif', 'Coupe / Roadster', 'Experience the AMG GT 4 Door Coupe by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG GT 4 Door Coupe'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG GT Coupe', '/cars/mercedes-benz-amg-gt-coupe.avif', 'Coupe / Roadster', 'Experience the AMG GT Coupe by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG GT Coupe'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG S 63', '/cars/mercedes-benz-amg-s-63.avif', 'Sedan', 'Experience the AMG S 63 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG S 63'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'AMG SL', '/cars/mercedes-benz-amg-sl.avif', 'Sedan', 'Experience the AMG SL by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('AMG SL'));
UPDATE cars SET image_url = '/cars/mercedes-benz-c-class.avif' WHERE id = 106;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'CLA Electric', '/cars/mercedes-benz-cla-electric.avif', 'Crossover / SUV', 'Experience the CLA Electric by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('CLA Electric'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'CLE Cabriolet', '/cars/mercedes-benz-cle-cabriolet.avif', 'Coupe / Roadster', 'Experience the CLE Cabriolet by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('CLE Cabriolet'));
UPDATE cars SET image_url = '/cars/mercedes-benz-e-class.avif' WHERE id = 107;
UPDATE cars SET image_url = '/cars/mercedes-benz-eqs.avif' WHERE id = 114;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'EQS SUV', '/cars/mercedes-benz-eqs-suv.avif', 'SUV', 'Experience the EQS SUV by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('EQS SUV'));
UPDATE cars SET image_url = '/cars/mercedes-benz-g-class.avif' WHERE id = 113;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'G-Class Electric', '/cars/mercedes-benz-g-class-electric.avif', 'SUV', 'Experience the G-Class Electric by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('G-Class Electric'));
UPDATE cars SET image_url = '/cars/mercedes-benz-gla.avif' WHERE id = 109;
UPDATE cars SET image_url = '/cars/mercedes-benz-glc.avif' WHERE id = 110;
UPDATE cars SET image_url = '/cars/mercedes-benz-gle.avif' WHERE id = 111;
UPDATE cars SET image_url = '/cars/mercedes-benz-gls.avif' WHERE id = 112;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'Maybach EQS SUV', '/cars/mercedes-benz-maybach-eqs-suv.avif', 'SUV', 'Experience the Maybach EQS SUV by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('Maybach EQS SUV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'Maybach GLS', '/cars/mercedes-benz-maybach-gls.avif', 'SUV', 'Experience the Maybach GLS by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '7 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('Maybach GLS'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'Maybach S-Class', '/cars/mercedes-benz-maybach-s-class.avif', 'Sedan', 'Experience the Maybach S-Class by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('Maybach S-Class'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'Maybach SL 680', '/cars/mercedes-benz-maybach-sl-680.avif', 'Crossover / SUV', 'Experience the Maybach SL 680 by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '2/4 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('Maybach SL 680'));
UPDATE cars SET image_url = '/cars/mercedes-benz-s-class.avif' WHERE id = 108;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 15, 'V Class', '/cars/mercedes-benz-v-class.avif', 'MPV', 'Experience the V Class by Mercedes-Benz. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 15 AND LOWER(model_name) = LOWER('V Class'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 12, 'Gravite', '/cars/nissan-gravite.avif', 'SUV', 'Experience the Gravite by Nissan. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 12 AND LOWER(model_name) = LOWER('Gravite'));
UPDATE cars SET image_url = '/cars/nissan-magnite.avif' WHERE id = 89;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 12, 'Tekton', '/cars/nissan-tekton.avif', 'SUV', 'Experience the Tekton by Nissan. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 12 AND LOWER(model_name) = LOWER('Tekton'));
UPDATE cars SET image_url = '/cars/nissan-x-trail.avif' WHERE id = 90;
UPDATE cars SET image_url = '/cars/range-rover.avif' WHERE id = 141;
UPDATE cars SET image_url = '/cars/range-rover-evoque.avif' WHERE id = 138;
UPDATE cars SET image_url = '/cars/range-rover-sport.avif' WHERE id = 140;
UPDATE cars SET image_url = '/cars/range-rover-velar.avif' WHERE id = 139;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 11, 'Duster', '/cars/renault-duster.avif', 'SUV', 'Experience the Duster by Renault. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 11 AND LOWER(model_name) = LOWER('Duster'));
UPDATE cars SET image_url = '/cars/renault-kwid.avif' WHERE id = 86;
UPDATE cars SET image_url = '/cars/renault-kiger.avif' WHERE id = 88;
UPDATE cars SET image_url = '/cars/renault-triber.avif' WHERE id = 87;
UPDATE cars SET image_url = '/cars/skoda-kodiaq.avif' WHERE id = 80;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 9, 'Kodiaq RS', '/cars/skoda-kodiaq-rs.avif', 'SUV', 'Experience the Kodiaq RS by Skoda. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 9 AND LOWER(model_name) = LOWER('Kodiaq RS'));
UPDATE cars SET image_url = '/cars/skoda-kushaq.avif' WHERE id = 78;
UPDATE cars SET image_url = '/cars/skoda-kylaq.avif' WHERE id = 77;
UPDATE cars SET image_url = '/cars/skoda-octavia-rs.avif' WHERE id = 81;
UPDATE cars SET image_url = '/cars/skoda-slavia.avif' WHERE id = 79;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Aeris', '/cars/tata-aeris.avif', 'Crossover / SUV', 'Experience the Aeris by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Aeris'));
UPDATE cars SET image_url = '/cars/tata-altroz.avif' WHERE id = 18;
UPDATE cars SET image_url = '/cars/tata-curvv.avif' WHERE id = 21;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Curvv EV', '/cars/tata-curvv-ev.avif', 'SUV', 'Experience the Curvv EV by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Curvv EV'));
UPDATE cars SET image_url = '/cars/tata-harrier.avif' WHERE id = 22;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Harrier EV', '/cars/tata-harrier-ev.avif', 'SUV', 'Experience the Harrier EV by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Harrier EV'));
UPDATE cars SET image_url = '/cars/tata-nexon.avif' WHERE id = 20;
UPDATE cars SET image_url = '/cars/tata-nexon-ev.avif' WHERE id = 25;
UPDATE cars SET image_url = '/cars/tata-punch.avif' WHERE id = 19;
UPDATE cars SET image_url = '/cars/tata-punch-ev.avif' WHERE id = 24;
UPDATE cars SET image_url = '/cars/tata-safari.avif' WHERE id = 23;
UPDATE cars SET image_url = '/cars/tata-sierra.avif' WHERE id = 27;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Sierra EV', '/cars/tata-sierra-ev.avif', 'SUV', 'Experience the Sierra EV by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Sierra EV'));
UPDATE cars SET image_url = '/cars/tata-tiago-ev.avif' WHERE id = 26;
UPDATE cars SET image_url = '/cars/tata-tiago.avif' WHERE id = 16;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Tigor EV', '/cars/tata-tigor-ev.avif', 'Crossover / SUV', 'Experience the Tigor EV by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Tigor EV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Xpres', '/cars/tata-xpres.avif', 'Crossover / SUV', 'Experience the Xpres by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Xpres'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Xpres T EV', '/cars/tata-xpres-t-ev.avif', 'Crossover / SUV', 'Experience the Xpres T EV by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'All-Electric', 'Automatic', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Xpres T EV'));
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 2, 'Yodha Pickup', '/cars/tata-yodha-pickup.avif', 'Pickup Truck', 'Experience the Yodha Pickup by Tata. Join Bangalore buyers for group discounts and dealer perks.', 'Diesel', 'Manual', '2/5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 2 AND LOWER(model_name) = LOWER('Yodha Pickup'));
UPDATE cars SET image_url = '/cars/toyota-camry.avif' WHERE id = 64;
UPDATE cars SET image_url = '/cars/toyota-fortuner.avif' WHERE id = 62;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 6, 'Fortuner Legender', '/cars/toyota-fortuner-legender.avif', 'SUV', 'Experience the Fortuner Legender by Toyota. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 6 AND LOWER(model_name) = LOWER('Fortuner Legender'));
UPDATE cars SET image_url = '/cars/toyota-glanza.avif' WHERE id = 56;
UPDATE cars SET image_url = '/cars/toyota-hilux.avif' WHERE id = 63;
UPDATE cars SET image_url = '/cars/toyota-innova-crysta.avif' WHERE id = 60;
UPDATE cars SET image_url = '/cars/toyota-innova-hycross.avif' WHERE id = 61;
UPDATE cars SET image_url = '/cars/toyota-land-cruiser-300.avif' WHERE id = 66;
UPDATE cars SET image_url = '/cars/toyota-rumion.avif' WHERE id = 58;
UPDATE cars SET image_url = '/cars/toyota-taisor.avif' WHERE id = 57;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 6, 'Urban Cruiser EBELLA', '/cars/toyota-urban-cruiser-ebella.avif', 'Crossover / SUV', 'Experience the Urban Cruiser EBELLA by Toyota. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 6 AND LOWER(model_name) = LOWER('Urban Cruiser EBELLA'));
UPDATE cars SET image_url = '/cars/toyota-urban-cruiser-hyryder.avif' WHERE id = 59;
UPDATE cars SET image_url = '/cars/toyota-vellfire.avif' WHERE id = 65;
UPDATE cars SET image_url = '/cars/volkswagen-golf-gti.avif' WHERE id = 85;
UPDATE cars SET image_url = '/cars/volkswagen-taigun.avif' WHERE id = 82;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 10, 'Tayron', '/cars/volkswagen-tayron.avif', 'Crossover / SUV', 'Experience the Tayron by Volkswagen. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 10 AND LOWER(model_name) = LOWER('Tayron'));
UPDATE cars SET image_url = '/cars/volkswagen-tiguan-r-line.avif' WHERE id = 84;
UPDATE cars SET image_url = '/cars/volkswagen-virtus.avif' WHERE id = 83;
UPDATE cars SET image_url = '/cars/volvo-ec40.avif' WHERE id = 126;
UPDATE cars SET image_url = '/cars/volvo-ex30.avif' WHERE id = 124;
UPDATE cars SET image_url = '/cars/volvo-ex40.avif' WHERE id = 125;
INSERT INTO cars (brand_id, model_name, image_url, body_type, description, fuel_type, transmission, seating, active, created_at)
SELECT 17, 'EX90', '/cars/volvo-ex90.avif', 'SUV', 'Experience the EX90 by Volvo. Join Bangalore buyers for group discounts and dealer perks.', 'Petrol / Diesel', 'Automatic / Manual', '5 Seater', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE brand_id = 17 AND LOWER(model_name) = LOWER('EX90'));
UPDATE cars SET image_url = '/cars/volvo-xc60.avif' WHERE id = 122;
UPDATE cars SET image_url = '/cars/volvo-xc90.avif' WHERE id = 123;
UPDATE cars SET image_url = '/cars/citroen-aircross-x.avif' WHERE id = 92;
UPDATE cars SET image_url = '/cars/citroen-basalt-x.avif' WHERE id = 93;
UPDATE cars SET image_url = '/cars/citroen-c3-x.avif' WHERE id = 91;
UPDATE cars SET image_url = '/cars/citroen-e-c3-x.avif' WHERE id = 94;
UPDATE cars SET image_url = '/cars/mahindra-be-6-sporteq.avif' WHERE id = 47;
UPDATE cars SET image_url = '/cars/mahindra-thar-og.avif' WHERE id = 41;
UPDATE cars SET image_url = '/cars/mahindra-scorpio.avif' WHERE id = 43;
UPDATE cars SET image_url = '/cars/mahindra-xuv-7xo.avif' WHERE id = 45;
UPDATE cars SET image_url = '/cars/tata-tigor-ev.avif' WHERE id = 17;
COMMIT;
