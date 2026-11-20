# Guia de uso --- Controle de Visitantes

Complementa o `guia_uso_parte1.md`. Cobre o registro de **entrada e saída**
de visitantes, que usa o que o grupo já tinha: `Visitante`, `Morador` e
`Autorizacao`.

## Regra principal

O visitante **só entra se um morador o autorizou para aquela data**
(requisito 3 do enunciado). A autorização continua sendo criada pelo
`AutorizacaoService`; o novo `ControleVisitaService` só consulta.

## Classes novas

| Classe | Função |
|---|---|
| `enums/StatusVisita` | `EM_ANDAMENTO` ou `CONCLUIDA` |
| `model/RegistroVisita` | Visitante, morador, entrada, saída e status (guarda só IDs, como `Reserva`) |
| `repository/RegistroVisitaRepository` | CSV em `data/registro_visita.csv` (criado sozinho) |
| `service/ControleVisitaService` | Regras de entrada, saída, histórico e resumo |
| `view/MenuVisitante` | Menu de console do módulo |

## Fluxo

```java
// montagem (mesmo padrão repositório + serviço)
RegistroVisitaRepository registros = new RegistroVisitaRepository();
ControleVisitaService controle =
        new ControleVisitaService(moradores, visitantes, autorizacoes, registros);

// 1) morador autoriza o visitante para o dia
autorizacaoService.cadastrarAutorizacao("11122233344", LocalDate.now(), "55566677788", "Carlos Souza");

// 2) na portaria
controle.registrarEntrada("55566677788");   // só um morador autorizou hoje
controle.registrarEntrada("55566677788", "11122233344"); // se mais de um autorizou

// 3) ao sair
controle.registrarSaida("55566677788");
```

## Erros (como no `ReservaService`)

O serviço **lança exceção** e não imprime nada; o menu mostra a mensagem.

| Situação | Exceção |
|---|---|
| CPF vazio, visitante ou morador inexistente, datas invertidas | `IllegalArgumentException` |
| Sem autorização para hoje | `IllegalStateException` |
| Entrada com o visitante já dentro | `IllegalStateException` |
| Saída de quem não está dentro | `IllegalStateException` |

Depois de sair, o visitante pode entrar de novo no mesmo dia (cada passagem é
um registro).

## Consultas

```java
controle.listarEmAndamento();                    // quem está dentro agora
controle.historicoPorPeriodo(de, ate);           // pela data de entrada
controle.historicoPorVisitante("55566677788");
controle.historicoPorMorador("11122233344");
controle.resumo(de, ate);                        // total, concluídas, em andamento, tempo médio
```

## Como abrir o menu

Enquanto o `Main` do grupo não está pronto, dá para rodar o menu sozinho
(como o `MenuReserva`): execute `view.MenuVisitante`. Quando o `Main` tiver
um menu geral, basta:

```java
new MenuVisitante().exibir();
```

## Testes

`ControleVisitaServiceTest` (11 testes) usa o `ServiceTestSupport`, que agora
também isola `registro_visita.csv`, e um relógio controlado para não depender
da hora real.
