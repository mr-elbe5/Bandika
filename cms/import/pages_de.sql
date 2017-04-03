-- de
SELECT ADDSITE (1, 100, 1, 'de', 'Home', 'homePage');
--
UPDATE t_locale SET home = '/de/', tree_id=100
WHERE locale = 'de';
-- test data
SELECT ADDSITE (100, 110, 1, 'konzept', 'Konzept', 'areaPage');
--
SELECT ADDSITE (100, 200, 2, 'bedienung', 'Bedienung', 'areaPage');
--
SELECT ADDSITE (200, 210, 1, 'leser', 'Leser', 'areaPage');
--
SELECT ADDSITE (200, 220, 2, 'redaktion', 'Redaktion', 'areaPage');
--
SELECT ADDSITE (200, 230, 3, 'administration', 'Administration', 'areaPage');
--
SELECT ADDSITE (100, 250, 3, 'programmierung', 'Programmierung', 'areaPage');
--
SELECT ADDSITE (250, 260, 1, 'basis', 'Basis', 'areaPage');
--
SELECT ADDSITE (250, 270, 2, 'struktur', 'Struktur', 'areaPage');
--
SELECT ADDSITE (250, 280, 3, 'module', 'Administration', 'areaPage');
--
SELECT ADDSITE (100, 300, 4, 'anleitung', 'Anleitung', 'areaPage');
--
SELECT ADDSITE (300, 310, 1, 'leser', 'Leser', 'areaPage');
--
SELECT ADDSITE (300, 320, 2, 'redaktion', 'Redaktion', 'areaPage');
--
SELECT ADDSITE (300, 330, 3, 'administration', 'Administration', 'areaPage');
--



