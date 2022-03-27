# PAW ITBA 2020 Project - Moovify

Moovify is a web forum that seeks to encourage the discussion of our favorite movies. Final implementation with Spring Framework, Jersey, Hibernate and AngularJS.

Live test: http://pawserver.it.itba.edu.ar/paw-2020b-3/

## Screenshots
![screencapture-pawserver-it-itba-edu-ar-paw-2020b-3-2022-03-27-14_49_11](https://user-images.githubusercontent.com/45410089/160294358-c586258d-905a-49e8-b940-41f2a964fec7.png)
![screencapture-pawserver-it-itba-edu-ar-paw-2020b-3-post-4-2022-03-27-14_49_34](https://user-images.githubusercontent.com/45410089/160294360-ef44164b-bf79-48e9-a97a-59e0d8b8800d.png)
![screencapture-pawserver-it-itba-edu-ar-paw-2020b-3-movie-3-2022-03-27-14_52_03](https://user-images.githubusercontent.com/45410089/160294368-fae7e97f-28fe-4579-a125-e0acfb53e464.png)
![screencapture-pawserver-it-itba-edu-ar-paw-2020b-3-user-2022-03-27-14_51_17](https://user-images.githubusercontent.com/45410089/160294371-57401727-b414-40c7-ab12-bcd19804dca1.png)


## Main Features

- Creation of posts about movies
- Creation of comments on posts
- Search engine for posts, movies and users with multiple filters
- Follow users and bookmark posts
- User creation and editing with email validation
- Post and comment editing
- Support for administrator accounts: deleting and restoring entities

## Technologies

Moovify is implemented primarily using the Spring Java framework and PostgresSQL for the database, following the MVC design pattern.
Each layer is strongly separated into Controllers, Services, Models, Interfaces and, eventually, Frontend modules.

The project had three distinct phases, where the technologies used were changing.

- Phase 1: Server-Side rendering using Spring MVC, JSP for HTML templates and JDBC for database management.

- Phase 2: JDBC was replaced by Hibernate.

- Phase 3: The server-side model was replaced by the API model. For this Spring MVC was replaced by Jersey, and the frontend is now implemented with Angularjs as a separate module. This is the final version of the project.

## Building

To build the project with Maven simply run the `mvn clean package` command. This includes running the tests for each module. Note that for the frontend module to build correctly, you need to have ruby, compass and libpng installed.

## Members

- [Brandy, Tobias](https://github.com/tobiasbrandy)
- [Comerci, Nicolas](https://github.com/ncomerci)
- [Pannunzio, Faustino](https://github.com/Fpannunzio)
- [Sagues, Ignacio](https://github.com/isagues)

## Access credentials

- User with mail not validated: notValidatedUser
- User with validated mail: validatedUser1
- Administrator user: adminUser

In all cases the password is: Paw123456789
