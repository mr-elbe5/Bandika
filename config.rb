require 'compass/import-once/activate'
# Require any additional compass plugins here.

# Set this to the root of your project when deployed:
http_path = "/"
add_import_path "webbase/scss"
css_dir = "cms/web/_statics/css"
sass_dir = "cms/scss"
images_dir = "cms/web/_statics/img"
http_images_path = "/_statics/img"
fonts_dir = "cms/web/_statics/fonts"
http_fonts_path = "/_statics/fonts"
javascripts_dir = "cms/web/_statics/js"
sourcemap = true

# You can select your preferred output style here (can be overridden via the command line):
# output_style = :expanded or :nested or :compact or :compressed
output_style = :compact

# To enable relative paths to assets via compass helper functions. Uncomment:
#relative_assets = true

# To disable debugging comments that display the original location of your selectors. Uncomment:
line_comments = false
