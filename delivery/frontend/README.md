As it contains a vue router plugin, only click on index.html will not work.

you can use node js server

npm install -g serve
# -s flag means serve it in Single-Page Application mode
# which deals with the routing problem below
serve -s dist


or copy dist content in www directory or on apache server then create a htaccess

<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  RewriteRule ^index\.html$ - [L]
  RewriteCond %{REQUEST_FILENAME} !-f
  RewriteCond %{REQUEST_FILENAME} !-d
  RewriteRule . /index.html [L]
</IfModule>


all queries have to be redirected to "index.htlml"

on nginx :

location / {
  try_files $uri $uri/ /index.html;
}

you can check all server configuration here 

https://router.vuejs.org/guide/essentials/history-mode.html

you can override ENV var, by default

VUE_APP_BACKEND_URL=http://localhost:8089