# Proyecto PAW ITBA 2020 2do Cuatrimestre - Moovify

Moovify es un foro web que busca fomentar la discucion sobre nuestras pelícuas favoritas. Implementacion final con Spring Framework, Jersey, Hibernate y AngularJS.

## Features Principales

- Creacion de posts sobre peliculas
- Creacion de comentarios en posts
- Motor de busqueda de posts, peliculas y usuarios con multiples filtros
- Seguir usuarios y marcar posts
- Creacion y edicion de usuario con validacion de mail
- Edicion de posts y comentarios
- Soporte para cuentas administradoras: borrado y restauracion de entidades

## Tecnologías

Moovify esta implementado principalmente utilizando el framework Spring de Java y PostgresSQL para la base de datos, siguiendo el patron de diseño MVC.
Cada capa esta fuertemente separada en los modulos Controllers, Services, Models, Interfaces y, eventualmente, Frontend.

El proyecto contó con tres fases distintas, donde las tecnologias utilizadas fueron cambiando.

    Fase 1: Server-Side rendering utilizando Spring MVC, JSP para los templates HTML y JDBC para el manejo de la base de datos.

    Fase 2: Se reemplazo JDBC por Hibernate.

    Fase 3: Se reemplazo el modelo server-side por el de API. Para esto Spring MVC fue reemplazado por Jersey, y el frontend ahora es implementado con Angularjs como un modulo aparte. Esta es la version final de proyecto.

## Building

Para buildear el proyecto con Maven simplemente correr el comando `mvn clean package`. Esto incluye correr los test de cada modulo. Notar que para que el modulo de frontend se buildee correctamente, se necesita tener instalado ruby, compass y libpng.

## Integrantes

- [Brandy, Tobias](https://github.com/tobiasbrandy)
- [Comerci, Nicolas](https://github.com/ncomerci)
- [Pannunzio, Faustino](https://github.com/Fpannunzio)
- [Sagues, Ignacio](https://github.com/isagues)

## Credenciales de acceso

- Usuario con mail sin validar: notValidatedUser
- Usuario con mail validado: validatedUser1
- Usuario administrador: adminUser

En todos los casos la contraseña es: Paw123456789
