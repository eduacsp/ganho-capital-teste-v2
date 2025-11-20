# Calculadora de Ganho de Capital

**Projeto para avaliação técnica – Processamento de operações de renda variável**

Este projeto implementa uma **Calculadora de Ganho de Capital** capaz de processar operações de compra e venda de ações e determinar o imposto devido.  
A aplicação recebe um ou mais blocos de operações em formato JSON via entrada padrão, processa cada bloco de forma independente e retorna o cálculo de imposto para cada operação.

O objetivo é demonstrar clareza de código, organização, domínio de lógica fiscal e qualidade arquitetural utilizando **Kotlin**, **Gradle** e boas práticas de engenharia de software.

## Requisitos

- **Kotlin** (JVM)
- **Java 17**
- **Gradle 8.5+**
- **Docker** (opcional)
- **Gson** – serialização customizada para `BigDecimal`

# 📦 Arquitetura do Projeto

A solução segue uma estrutura baseada em **Domain + Services + Chain of Responsibility**, com lógica fiscal isolada e totalmente testável:

```bash
src/
└─ main/kotlin/
├─ config/ → Carregamento de configurações
├─ context/ → Regras fiscais e cálculo de imposto
├─ domain/ → Modelos de domínio (Operacao, Resultado, Contexto)
├─ enums/ → Enum de tipos de operação
├─ handler/ → Handlers BUY e SELL (Chain of Responsibility)
├─ service/ → Serviço principal (GanhoCapitalService)
├─ util/ → Serialização/deserialização BigDecimal
└─ GanhoCapitalMain.kt → Entrada da aplicação
```

# 🔍 Principais Classes

### **`GanhoCapitalMain.kt`**
- Lê entrada via `stdin`
- Suporta múltiplos blocos JSON
- Desserializa operações
- Usa `GanhoCapitalServiceImpl` para processar
- Serializa a saída
- Exibe mensagens claras de erro

---

### **`GanhoCapitalServiceImpl.kt`**
- Encadeia os handlers (`CompraHandler` → `VendaHandler`)
- Mantém estado no `OperacaoContext`
- Retorna lista de `Resultado` para cada operação

---

### **`CompraHandler.kt`**
Responsável por:
- Atualizar quantidade total de ações
- Recalcular média ponderada
- Retornar imposto zero

---

### **`VendaHandler.kt`**
Responsável por:
- Validar disponibilidade de ações
- Calcular ganho bruto
- Compensar prejuízo acumulado
- Atualizar prejuízo acumulado
- Calcular imposto via `ImpostoCalculator`

---

### **`ImpostoCalculator.kt`**
Regras fiscais implementadas:
1. Compensação automática de prejuízo acumulado
2. Aplicação da taxa de 20% **somente** quando:
    - há ganho líquido positivo
    - o valor da venda supera R$ 20.000
3. Arredondamento com `HALF_UP`

---

### **`AppConfig`**
Carrega parâmetros de `application.properties`, permitindo ajustes sem alterar o código:
- taxa.imposto
- valor.isencao
- escala.padrao
- ganho.zerado
- retorno.zero


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

1. **Chain of Responsibility**

Permite adicionar novas regras sem alterar lógica existente (OCP).

2. **Uso seguro de BigDecimal**

Serialização customizada evita inconsistências entre JSON e cálculo fiscal.

3. **Contexto de Operações**

Isola o estado da carteira, mantendo domínio claro e testável.

4. **Configuração via application.properties**

Permite simulações com diferentes taxas ou valores de isenção.

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

