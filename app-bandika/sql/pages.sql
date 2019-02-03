-- en
SELECT ADDPAGE(1, 500, 1, 'en', 'Home', 'defaultMaster', 'homePage');
--
UPDATE t_locale
SET home = '/en.html', home_id = 500
WHERE locale = 'en';
-- test data
SELECT ADDPAGE(500, 510, 1, 'concept', 'Concept', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(500, 600, 2, 'usage', 'Usage', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(600, 610, 1, 'reader', 'Reader', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(600, 620, 2, 'editor', 'Editor', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(600, 630, 3, 'administrator', 'Administrator', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(500, 650, 3, 'coding', 'Coding', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(650, 660, 1, 'basis', 'Basis', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(650, 670, 2, 'structure', 'Structure', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(650, 680, 3, 'modules', 'Modules', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(500, 700, 4, 'guide', 'Guide', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(700, 710, 1, 'reader', 'Reader', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(700, 720, 2, 'editor', 'Editor', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(700, 730, 3, 'administrator', 'Administrator', 'defaultMaster', 'fullPage');
-- de
SELECT ADDPAGE(1, 100, 1, 'de', 'Home', 'defaultMaster', 'homePage');
--
UPDATE t_locale
SET home = '/de.html', home_id = 100
WHERE locale = 'de';
-- test data
SELECT ADDPAGE(100, 110, 1, 'konzept', 'Konzept', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(100, 200, 2, 'bedienung', 'Bedienung', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(200, 210, 1, 'leser', 'Leser', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(200, 220, 2, 'redaktion', 'Redaktion', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(200, 230, 3, 'administration', 'Administration', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(100, 250, 3, 'programmierung', 'Programmierung', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(250, 260, 1, 'basis', 'Basis', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(250, 270, 2, 'struktur', 'Struktur', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(250, 280, 3, 'module', 'Administration', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(100, 300, 4, 'anleitung', 'Anleitung', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(300, 310, 1, 'leser', 'Leser', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(300, 320, 2, 'redaktion', 'Redaktion', 'defaultMaster', 'fullPage');
--
SELECT ADDPAGE(300, 330, 3, 'administration', 'Administration', 'defaultMaster', 'fullPage');

