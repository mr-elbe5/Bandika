-- templates must exist!!
-- de
SELECT ADDPAGE(1, 100, 1, 'Home', 'defaultMaster', 'homePage');
-- this must be the default locale!!
UPDATE t_locale
SET home_id = 100
WHERE locale = 'en';

