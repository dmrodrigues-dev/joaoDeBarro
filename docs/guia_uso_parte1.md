# Guia de uso --- João de Barro

Este documento explica como utilizar as classes já implementadas no
projeto João de Barro. A ideia é servir como referência para os
integrantes do grupo que forem integrar, testar ou ampliar o sistema.

## 1. Visão geral

O projeto está organizado em quatro partes principais:

  -----------------------------------------------------------------------
  Pacote                              Responsabilidade
  ----------------------------------- -----------------------------------
  `model`                             Representa os dados e algumas
                                      regras de negócio, como moradores,
                                      apartamentos, reservas e cobranças.

  `repository`                        Consulta, cadastra e remove dados.
                                      A maioria dos repositórios persiste
                                      os dados em arquivos CSV.

  `service`                           Executa os casos de uso e valida
                                      regras antes de chamar os
                                      repositórios.

  `enums`                             Define valores fechados para o
                                      status das reservas, o status das
                                      cobranças e os tipos de cobrança.
  -----------------------------------------------------------------------

O ponto de entrada `Main.java` ainda está vazio. Portanto, atualmente,
as funcionalidades são utilizadas criando os repositórios e os serviços
no código. Este guia mostra esse padrão; ele não pressupõe que já exista
uma interface de terminal ou gráfica.

## 2. Como preparar o projeto

-   Utilize um JDK compatível com a versão configurada no `pom.xml`
    (Java 21).
-   Abra o projeto pela pasta que contém o `pom.xml`.
-   O Maven gerencia a dependência OpenCSV, utilizada para ler e gravar
    os arquivos CSV.
-   Os caminhos de dados são relativos ao diretório de execução do
    projeto. Execute o programa a partir da raiz do projeto para que a
    pasta `data/` seja encontrada corretamente.

Os repositórios criam os arquivos CSV ausentes quando são inicializados.
Os arquivos de dados usados atualmente são:

  Arquivo                  Conteúdo
  ------------------------ ----------------------------
  `data/apartamento.csv`   Apartamentos
  `data/morador.csv`       Moradores
  `data/visitante.csv`     Visitantes
  `data/autorizacao.csv`   Autorizações de visitantes
  `data/reserva.csv`       Reservas de áreas comuns
  `data/cobranca.csv`      Cobranças

**Importante:** os CSVs são persistentes. Encerrar e abrir o programa
não apaga os dados. Antes de editar ou apagar arquivos manualmente, faça
uma cópia de segurança. Não use os arquivos reais de dados para testes
automatizados que possam limpá-los ou sobrescrevê-los.

## 3. Padrão de utilização: repositório + serviço

Os serviços recebem seus repositórios pelo construtor. Primeiro
instanciamos os repositórios e depois os serviços que dependem deles.

``` java
import repository.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        ApartamentoRepository apartamentos = new ApartamentoRepository();
        MoradorRepository moradores = new MoradorRepository();
        VisitanteRepository visitantes = new VisitanteRepository();
        AutorizacaoRepository autorizacoes = new AutorizacaoRepository();
        ReservaRepository reservas = new ReservaRepository();
        CobrancaRepository cobrancas = new CobrancaRepository();

        ApartamentoService apartamentoService =
                new ApartamentoService(apartamentos);

        MoradorService moradorService =
                new MoradorService(apartamentos, moradores);

        VisitanteService visitanteService =
                new VisitanteService(visitantes);

        AutorizacaoService autorizacaoService =
                new AutorizacaoService(moradores, visitantes, autorizacoes);

        ReservaService reservaService =
                new ReservaService(reservas, cobrancas);
    }
}
```

Esse trecho mostra a montagem dos objetos; ele não executa cadastros por
si só. Em uma aplicação com menu, mantenha essas instâncias disponíveis
enquanto o programa estiver em execução, em vez de recriar os
repositórios a cada operação.

## 4. Apartamentos

Classe de serviço: `ApartamentoService`

### Cadastrar

``` java
apartamentoService.cadastrarApartamento("Torre A", "101");
```

O cadastro é recusado se já existir um apartamento com a mesma
combinação de torre e número. O mesmo número pode existir em torres
diferentes.

### Consultar por torre e número

``` java
Apartamento apartamento =
        apartamentoService.buscarPorNumeroTorre("Torre A", "101");

if (apartamento != null) {
    System.out.println("ID: " + apartamento.getId());
    System.out.println("Torre: " + apartamento.getTorre());
    System.out.println("Número: " + apartamento.getNumero());
}
```

O método retorna `null` se não encontrar o apartamento. O ID é atribuído
pelo repositório ao salvar o objeto; use o ID retornado na consulta para
relacionar um morador ao apartamento.

### Listar e remover

``` java
apartamentoService.listarTodos();
apartamentoService.removerApartamento("Torre A", "101");
```

A remoção procura o apartamento pela torre e pelo número. Atualmente, o
serviço não verifica se existem moradores associados antes de remover um
apartamento.

## 5. Moradores

Classe de serviço: `MoradorService`

### Cadastrar um morador

O apartamento deve estar cadastrado. Obtenha seu ID consultando o
repositório ou o serviço de apartamentos:

``` java
Apartamento apartamento =
        apartamentoService.buscarPorNumeroTorre("Torre A", "101");

if (apartamento != null) {
    moradorService.cadastrarMorador(
            "Ana Silva",
            "11122233344",
            apartamento.getId(),
            1,  // quantidade de pets
            1   // vagas de garagem
    );
}
```

O serviço verifica se o CPF já está cadastrado e se o apartamento
informado existe. O CPF é tratado como texto (`String`), não como
número; isso preserva zeros à esquerda quando houver.

### Buscar, listar e remover

``` java
Morador morador = moradorService.buscarPorCpf("11122233344");
moradorService.listarTodos();
moradorService.removerMorador("11122233344");
```

`buscarPorCpf` retorna o objeto `Morador` ou `null` quando não encontra.
O vínculo do morador é armazenado pelo campo `apartamentoId`; o modelo
não mantém uma referência direta ao objeto `Apartamento`.

**Atenção para quem estiver integrando o código:** na versão analisada,
a verificação de existência do apartamento em
`MoradorService.cadastrarMorador` compara o resultado de
`buscarPorId(...)` com `null`. O `buscarPorId` atual de `CsvRepository`
retorna o próprio objeto ou `null`, então essa comparação corresponde à
implementação atual. Se esse método for alterado futuramente para
retornar `Optional`, a validação deverá ser adaptada.

## 6. Visitantes

Classe de serviço: `VisitanteService`

### Cadastrar e consultar

``` java
visitanteService.cadastrarVisitante("Carlos Souza", "55566677788");

Visitante visitante =
        visitanteService.buscarPorCpf("55566677788");

visitanteService.listarTodos();
```

O serviço não cadastra outro visitante com o mesmo CPF. A busca retorna
`null` quando não encontra o CPF.

### Remover

``` java
visitanteService.removerVisitante("55566677788");
```

A remoção é feita pelo CPF, após localizar o visitante.

## 7. Autorizações de visitantes

Classe de serviço: `AutorizacaoService`

Uma autorização liga um morador, um visitante e uma data. Para
cadastrar, informe o CPF do morador, a data, o CPF do visitante e o nome
do visitante.

``` java
import java.time.LocalDate;

LocalDate dia = LocalDate.of(2026, 11, 1);

autorizacaoService.cadastrarAutorizacao(
        "11122233344", // CPF do morador
        dia,
        "55566677788", // CPF do visitante
        "Carlos Souza"
);
```

Comportamento implementado: - Se o morador não existir, a operação é
interrompida. - Se o visitante não existir, ele é cadastrado com o nome
e o CPF informados. - Se já existir uma autorização para o mesmo
morador, visitante e dia, não é criada outra. - O mesmo visitante pode
ter autorização em outra data.

### Consultar autorizações

``` java
autorizacaoService.buscarPorVisitanteDia(
        "55566677788",
        LocalDate.of(2026, 11, 1)
);

Autorizacao autorizacao =
        autorizacaoService.buscarPorMoradorVisitanteDia(
                "11122233344",
                "55566677788",
                LocalDate.of(2026, 11, 1)
        );

autorizacaoService.listarTodos();
```

`buscarPorVisitanteDia` retorna uma lista.
`buscarPorMoradorVisitanteDia` retorna uma autorização; se não encontrar
o morador, visitante ou autorização, o método retorna um objeto
`Autorizacao` vazio, sem ID atribuído. Ao verificar o resultado, confira
`getId()` ou valide previamente os cadastros.

### Remover autorização

``` java
autorizacaoService.removerAutorizacao(
        "11122233344",
        LocalDate.of(2026, 11, 1),
        "55566677788"
);
```

A remoção localiza a autorização usando os três dados: morador, data e
visitante.

## 8. Áreas comuns

A classe abstrata `AreaComum` define o nome, o ID, a taxa e o limite de
horas. As implementações disponíveis são:

  Classe              Taxa por reserva   Máximo de horas
  ----------------- ------------------ -----------------
  `Churrasqueira`            R\$ 80,00                 4
  `Academia`                  R\$ 0,00                 2
  `Piscina`                   R\$ 0,00                 2
  `SalaoFestas`             R\$ 150,00                 6

Os valores estão definidos nos próprios modelos. O `AreaComumRepository`
fornece uma lista fixa dessas áreas com IDs de 1 a 4. O `ReservaService`
recebe um objeto `AreaComum` diretamente; ele não consulta
automaticamente o `AreaComumRepository`.

Exemplo:

``` java
AreaComum churrasqueira = new Churrasqueira(1L);
AreaComum academia = new Academia(2L);
```

Use os IDs definidos para as áreas fixas se for necessário manter
consistência entre operações. Não crie IDs diferentes para representar a
mesma área sem uma decisão do grupo.

## 9. Reservas

Classe de serviço: `ReservaService`

Para reservar, são necessários um objeto `Morador`, um objeto
`AreaComum`, a quantidade de horas, o horário inicial e a data.

``` java
import java.time.LocalDate;
import java.time.LocalTime;

Morador morador = moradorService.buscarPorCpf("11122233344");

if (morador != null) {
    reservaService.reservarAreaComum(
            morador,
            new Churrasqueira(1L),
            2,
            LocalTime.of(10, 0),
            LocalDate.of(2026, 11, 1)
    );
}
```

O serviço aplica estas regras:
1. A quantidade de horas deve ser maior
que zero e não pode ultrapassar o limite da área.
2. Não pode haver
sobreposição de horário com outra reserva cujo status seja `RESERVADO`
para a mesma área e data.
3. Horários adjacentes são permitidos. Por
exemplo, uma reserva das 10h às 12h não conflita com outra que comece às
12h.
4. O morador não pode ter cobrança pendente atrasada há 30 dias ou
mais, conforme a verificação implementada.
5. Se a reserva for aceita, o
sistema salva a reserva e cria automaticamente uma cobrança associada a
ela, com vencimento 30 dias após a data da reserva e valor definido pela
área.

A taxa de reserva é criada até mesmo quando o valor da área é zero. A
regra de conflito considera reservas com status `RESERVADO`; os enums já
incluem `CONCLUIDO` e `CANCELADO`, mas não há, no serviço atual, métodos
específicos para concluir ou cancelar reservas.

### Consultar reservas e cobranças

``` java
reservas.listarTudo();
cobrancas.listarTudo();
```

Essas consultas são feitas diretamente nos repositórios, que retornam
listas. Para encontrar reservas de uma área em uma data específica, o
repositório também possui:

``` java
reservas.buscarPorAreaData(
        1L,
        LocalDate.of(2026, 11, 1)
);
```

## 10. Cobranças

Classe de modelo: `Cobranca`; repositório: `CobrancaRepository`.

O `ReservaService` gera cobranças de reserva automaticamente. O modelo
também permite representar cobranças condominiais e de cancelamento por
meio de `TipoCobranca`:

-   `TAXA_CONDOMINIAL`
-   `TAXA_RESERVA`
-   `CANCELAMENTO_RESERVA`

Os status possíveis são:

-   `PENDENTE`
-   `PAGO`
-   `CANCELADO`

### Verificar atraso e calcular encargos

``` java
LocalDate hoje = LocalDate.now();

Cobranca cobranca = cobrancas.listarTudo().get(0);

boolean atrasada = cobranca.estaAtrasada(hoje);
long dias = cobranca.diasAtraso(hoje);
BigDecimal valorAtualizado = cobranca.valorComEncargos(hoje);
```

Uma cobrança é considerada atrasada quando está `PENDENTE` e a data
consultada é posterior ao vencimento. No cálculo implementado, os
encargos são: - multa de 2% sobre o valor original; - juros de 0,033%
por dia de atraso (fator `0.00033` por dia); - arredondamento para duas
casas decimais.

O cálculo não altera o valor armazenado na cobrança; retorna um
`BigDecimal` calculado para a data informada.

`CobrancaRepository.buscarPendentesMorador(moradorId, dia)` retorna as
cobranças do morador que estejam atrasadas na data consultada. Apesar do
nome, o método retorna apenas as cobranças que passam pela condição de
atraso, não todas as cobranças pendentes.

**Limitação atual:** não existe `CobrancaService` nem um método de
repositório específico para registrar o pagamento de uma cobrança e
persistir essa alteração. Alterar `setStatus(StatusCobranca.PAGO)` em um
objeto carregado não grava automaticamente o CSV. Essa funcionalidade
precisa ser implementada se fizer parte do escopo do grupo.

## 11. Como funcionam os repositórios CSV

Todos os repositórios que herdam de `CsvRepository` recebem o caminho do
CSV e a classe do modelo. Ao serem instanciados, carregam os registros
existentes. Os métodos comuns são:

``` java
repository.listarTudo();      // lista imutável dos registros
repository.buscarPorId(1L);   // objeto ou null
repository.salvar(entidade);  // atribui ID se necessário e grava o CSV
repository.remover(1L);       // remove pelo ID e grava o CSV
```

`listarTudo()` devolve uma cópia não modificável da lista. Para
cadastrar ou remover dados, utilize os métodos do serviço correspondente
quando houver um serviço disponível; assim as validações de negócio são
executadas.

O método `salvar` atribui um ID sequencial se a entidade ainda não tiver
ID. Ele adiciona o objeto à lista e regrava os dados no arquivo CSV. Os
arquivos são a persistência atual do projeto; não há banco de dados
relacional implementado.

## 12. Datas, horários e valores monetários

-   Use `LocalDate` para datas, por exemplo `LocalDate.of(2026, 11, 1)`.
-   Use `LocalTime` para horários, por exemplo `LocalTime.of(10, 30)`.
-   Use `BigDecimal` para valores monetários; evite `double` para
    representar dinheiro.
-   As datas são serializadas em CSV no formato `yyyy-MM-dd`; o horário
    de início da reserva utiliza `HH:mm`.
-   Os métodos de cobrança recebem uma data como argumento, o que
    permite testar regras de atraso sem depender exclusivamente da data
    atual.

## 13. Testes automatizados

Os testes estão em `src/test/java`. Eles usam JUnit e cobrem regras dos
modelos e serviços, incluindo: - cadastro, consulta, duplicidade e
remoção de apartamentos, moradores e visitantes; - autorizações por
morador, visitante e data; - limite de horas, conflitos de horário e
cobrança gerada na reserva; - atraso e encargos de cobranças.

Os testes de serviço usam `ServiceTestSupport`, que faz cópia dos CSVs
existentes e prepara arquivos isolados antes de cada teste, restaurando
os originais ao final. Execute os testes sequencialmente, pois os
repositórios utilizam caminhos CSV compartilhados. Antes de executar
testes ou alterar essa classe, confirme que os arquivos de dados
importantes estão respaldados.

Execute a suíte pela raiz do projeto:

``` bash
mvn test
```

## 14. Responsabilidades e limitações a conhecer

Antes de acrescentar funcionalidades, considere o comportamento da
versão atual:

-   `Main.java` ainda não implementa menu ou interface.
-   Os serviços imprimem mensagens no console e, em geral, retornam
    `void` nos cadastros. Ainda não foram implementados exceções customizadas.
-   `AreaComumRepository` mantém uma lista fixa de áreas comuns; não
    existe cadastro dinâmico de novas áreas.
-   Não existe ainda um serviço próprio de cobranças nem fluxo completo
    de pagamento/cancelamento.
-   A remoção de apartamento não verifica vínculo com moradores.
-   As validações e mensagens descritas neste documento correspondem ao
    código presente no projeto na data em que este guia foi preparado.
    Se os serviços mudarem, atualize também este documento.

## 15. Fluxo recomendado para uma demonstração

Uma demonstração simples do que já está implementado pode seguir esta
ordem:

1.  Cadastrar um apartamento.
2.  Consultar o apartamento para obter seu ID.
3.  Cadastrar um morador associado ao ID do apartamento.
4.  Cadastrar uma autorização para um visitante em uma data.
5.  Consultar a autorização por visitante e data.
6.  Reservar uma área comum para o morador.
7.  Consultar as reservas e a cobrança criada automaticamente.
8.  Consultar a cobrança e demonstrar a verificação de atraso e o
    cálculo de encargos.

Essa sequência respeita as dependências entre os dados: o morador
precisa de um apartamento existente, a autorização precisa de um morador
existente, e a reserva utiliza um morador e uma área comum.
