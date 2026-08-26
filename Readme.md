# Automação de Testes API - Restful-Booker

## Resumo do Projeto

Este repositório contém a implementação de uma suíte de testes automatizados de API desenvolvida para o ecossistema do Restful-Booker. O projeto aplica práticas modernas de Qualidade de Software (QA), estruturado com Java 25, RestAssured e JUnit 5, validando contratos (JSON Schema), operações de CRUD e fluxos de exceção. A arquitetura reflete a progressão dos desafios técnicos práticos propostos pela DIO, estabelecendo um padrão profissional de testes back-end.

## Tecnologias e Ferramentas

* **Java 25** (Utilização nativa de Records para payloads imutáveis)
* **RestAssured 5.x** (Motor de requisições e validações HTTP)
* **JUnit 5 (Jupiter)** (Orquestração da esteira de testes)
* **Maven** (Gerenciador de build e dependências)
* **Allure Framework** (Geração do dashboard visual de evidências)
* **DataFaker** (Injeção dinâmica de massa de testes)
* **Postman** (Testes exploratórios e mapeamento inicial)

## Estrutura de Destaque

* `DIO_QA.postman_collection.json`: Collection do Postman contendo o mapeamento das rotas e scripts básicos de validação, utilizada na fase de exploração manual.
* `video.webm`: Gravação demonstrando a execução via terminal da suíte de testes e a renderização interativa do Allure Report.
* `src/test/resources/schemas/`: Armazenamento isolado dos arquivos de validação de contrato.

## Tutorial de Configuração e Execução

### 1. Pré-requisitos

* **JDK 25** configurado no `PATH` do sistema operacional.
* **Apache Maven** (versão 3.8 ou superior).

### 2. Configuração de Variáveis de Ambiente

Visando a segurança da aplicação e a integridade do repositório, credenciais reais não são comitadas. Para rodar a aplicação localmente, crie o arquivo `config.properties` dentro do diretório `src/test/resources/` contendo a URL base pública (disponibilizada para estudos) e os dados de acesso temporários da API:

```properties
# Base da API pública para estudos
api.base.uri=https://restful-booker.herokuapp.com

# Insira os dados de acesso válidos (Não suba este arquivo para o Git)
api.auth.username=SEU_USUARIO_AQUI
api.auth.password=SUA_SENHA_AQUI

```

### 3. Execução dos Testes Automatizados

Abra o terminal na raiz do projeto (mesmo nível do arquivo `pom.xml`) e execute o comando abaixo para realizar o build, baixar as dependências e iniciar a suíte:

```bash
mvn clean test

```

### 4. Geração do Relatório (Allure Report)

Após a conclusão dos testes (sejam eles aprovados ou falhos), suba o servidor local de renderização das evidências executando:

```bash
mvn allure:serve

```

O navegador padrão será aberto automaticamente com o dashboard de qualidade, agrupando métricas, classes e logs detalhados de *Request* e *Response*.

---

**Autor:** Álan Bruno Rios Miguel