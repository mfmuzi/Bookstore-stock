## Bookstore stock

​	Neste projeto foi desenvolvida uma API REST de gerenciamento de estoque de livros em Spring Boot, incluindo as funcionalidades de incremento e decremento do estoque.

​	Também foram desenvolvidos testes unitários para validação dessa API, incluindo os testes que não foram demonstrados em aula.



### Rodando o projeto

Para executar o projeto no terminal, digite o seguinte comando:

```shell script
mvn spring-boot:run 
```

Após executar o comando acima, basta abrir o seguinte endereço e visualizar a execução do projeto:

```
http://localhost:8080/api/books
```



### Interagindo com o projeto

Para visualizar, adicionar, deletar e mudar a quantidade no estoque, utilize o Postman ou Insomnia:

Para adicionar um Livro use o POST em:

```
http://localhost:8080/api/books
```

Passando as propriedades:

```
{
    "title": "",
    "author": "",
    "max": ,
    "quantity": ,
    "genre": ""
}
```

Para visualizar o estoque use o GET em:

```
http://localhost:8080/api/books (Lista de Livros)
http://localhost:8080/api/books/{nome} (Busca por nome)
```

Para deletar um Livro use o DELETE em:

```
http://localhost:8080/api/books/{id}
```

Para fazer o incremento do estoque use o PATCH em:

```
http://localhost:8080/api/books/{id}/increment
```

Para fazer o decremento do estoque use o PATCH em:

```
http://localhost:8080/api/books/{id}/decrement
```



### Executando Testes

Para executar os testes desenvolvidos, basta executar o seguinte comando:

```shell script
mvn clean test
```

