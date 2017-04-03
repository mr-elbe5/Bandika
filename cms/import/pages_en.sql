-- en
SELECT ADDSITE (1, 500, 1, 'en', 'Home', 'homePage');
--
UPDATE t_locale SET home = '/en/', tree_id=500
WHERE locale = 'en';
-- test data
SELECT ADDSITE (500, 510, 1, 'concept', 'Concept', 'areaPage');
--
SELECT ADDSITE (500, 600, 2, 'usage', 'Usage', 'areaPage');
--
SELECT ADDSITE (600, 610, 1, 'reader', 'Reader', 'areaPage');
--
SELECT ADDSITE (600, 620, 2, 'editor', 'Editor', 'areaPage');
--
SELECT ADDSITE (600, 630, 3, 'administrator', 'Administrator', 'areaPage');
--
SELECT ADDSITE (500, 650, 3, 'coding', 'Coding', 'areaPage');
--
SELECT ADDSITE (650, 660, 1, 'basis', 'Basis', 'areaPage');
--
SELECT ADDSITE (650, 670, 2, 'structure', 'Structure', 'areaPage');
--
SELECT ADDSITE (650, 680, 3, 'modules', 'Modules', 'areaPage');
--
SELECT ADDSITE (500, 700, 4, 'guide', 'Guide', 'areaPage');
--
SELECT ADDSITE (700, 710, 1, 'reader', 'Reader', 'areaPage');
--
SELECT ADDSITE (700, 720, 2, 'editor', 'Editor', 'areaPage');
--
SELECT ADDSITE (700, 730, 3, 'administrator', 'Administrator', 'areaPage');
--
UPDATE t_configuration SET config_value='en'
WHERE config_key='defaultLocale';
