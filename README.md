# Ganho Capital Teste

Este projeto é uma Calculadora de Ganho de Capital para operações de compra e venda de ações. Ele processa uma série de operações e calcula os impostos devidos com base nas regulamentações fiscais brasileiras. A aplicação lê um JSON contendo as operações e retorna um JSON com os impostos calculados para cada operação.

## Requisitos

* Java 17
* Gradle 8.5 ou superior
* Docker (opcional)

## Arquivos Principais

- [`GanhoCapitalMain.kt`](src/main/kotlin/br/com/codingtest/GanhoCapitalMain.kt): Ponto de entrada da aplicação para leitura da entrada padrão, processamento de blocos independentes, desserialização do JSON, invoca o serviço GanhoCapitalServiceImpl para processar as operações e serialização da Saída
- [`GanhoCapitalServiceImpl.kt`](src/main/kotlin/br/com/codingtest/service/GanhoCapitalServiceImpl.kt): Responsável por processar uma lista de operações de compra e venda de ações
- [`CompraHandler.kt`](src/test/kotlin/br/com/codingtest/handler/CompraHandler.kt): Classe que atualiza a média ponderada do preço das ações e a quantidade total no contexto
- [`VendaHandler.kt`](src/test/kotlin/br/com/codingtest/handler/VendaHandler.kt): Classe que verifica se há ações suficientes para vender, calcula o ganho ou prejuízo da operação e ajusta o prejuízo acumulado no contexto. Se houver lucro, deduz eventuais prejuízos acumulados antes de calcular o imposto devido.
- [`OperacaoContext.kt`](src/test/kotlin/br/com/codingtest/context/OperacaoContext.kt): Objeto ImpostoCalculator responsável pelo cálculo do imposto sobre ganho de capital. Ela possui duas funções principais: Calcula o imposto aplicando a taxa sobre o ganho líquido e Reduz o prejuízo acumulado do ganho bruto antes de calcular o imposto

## Como Executar

### Usando o Docker

#### 1. Construir a Imagem Docker

No diretório raiz do projeto, execute:

```bash
docker build -t ganho-capital-app .
```

#### 2. Executar o Contêiner Docker

```bash
docker run -i ganho-capital-app
```

#### 3. Fornecer a Entrada

Após iniciar o contêiner, insira as operações em formato JSON. Por exemplo:

```json
[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
 {"operation":"sell", "unit-cost":50.00, "quantity": 10000},
 {"operation":"buy", "unit-cost":20.00, "quantity": 10000},
 {"operation":"sell", "unit-cost":50.00, "quantity": 10000}]
```

Pressione Enter duas vezes para sinalizar o fim da entrada.

### Usando o IntelliJ IDEA

#### 1. Importar o Projeto

* Abra o IntelliJ IDEA.
* Clique em **Open** e selecione o diretório raiz do projeto.

#### 2. Construir o Projeto

* Certifique-se de que o JDK correto (Java 17 e o Gradle 8.5) esteja selecionado nas configurações do projeto.
* Utilize as tarefas do Gradle ou as opções de **Build** do IntelliJ para compilar o projeto.

#### 3. Executar a Aplicação

* Execute a função `main` em `GanhoCapitalMain.kt`.
* Forneça a entrada no console quando solicitado.

### Executando com Arquivo de Entrada

Caso prefira fornecer os dados através de um arquivo JSON, siga os passos:

1. Crie um arquivo `input.json` no diretório raiz do projeto e adicione o seguinte conteúdo:

   ```json
   [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
    {"operation":"sell", "unit-cost":50.00, "quantity": 10000},
    {"operation":"buy", "unit-cost":20.00, "quantity": 10000},
    {"operation":"sell", "unit-cost":50.00, "quantity": 10000}]
   ```

2. Execute o comando abaixo para redirecionar a entrada para a aplicação:

   ```bash
   java -jar build/libs/ganho-capital-app.jar < input.json
   ```

## Executando os Testes

Para garantir a confiabilidade da aplicação, há testes automatizados implementados.

Para executá-los, utilize:

```bash
./gradlew test
```

Os resultados dos testes estarão disponíveis no diretório `build/reports/tests/test/index.html`.

## Decisões de Design

1. **Entrada via JSON**: O formato JSON foi escolhido para facilitar a integração com outras aplicações e permitir testes automatizados de maneira mais simples.
2. **Cálculo Baseado em Preço Médio Ponderado**: O sistema utiliza essa abordagem para determinar o custo das ações, seguindo as regras de tributação da Receita Federal.
3. **Uso do Pattern Chain of Responsibility**: O cálculo do ganho de capital foi estruturado utilizando o padrão de projeto **Chain of Responsibility**. Esse padrão foi escolhido para tornar o processamento de regras fiscais mais flexível e extensível. Com ele, é possível adicionar novas regras de tributação ou alterar a ordem de aplicação das regras sem modificar a lógica central da aplicação, garantindo melhor manutenibilidade e escalabilidade do código.
4. **Execução via Pipeline**: A aplicação permite o redirecionamento de entrada/saída para uso em pipelines de automação.

## Exemplo de Entrada e Saída

### Entrada

```json
[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},
 {"operation":"sell", "unit-cost":50.00, "quantity": 10000},
 {"operation":"buy", "unit-cost":20.00, "quantity": 10000},
 {"operation":"sell", "unit-cost":50.00, "quantity": 10000}]
```

### Saída

```json
[{"tax":0.00},
 {"tax":80000.00},
 {"tax":0.00},
 {"tax":60000.00}]
```

## Licença

[MIT](https://choosealicense.com/licenses/mit/)
