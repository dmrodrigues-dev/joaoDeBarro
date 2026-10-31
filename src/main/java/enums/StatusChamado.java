
package enums;

/*
 * Enum responsável por definir os estados
 * possíveis de um chamado de manutenção.
 *
 * Um enum evita que sejam utilizados
 * estados diferentes dos permitidos.
 */
public enum StatusChamado {

    // O chamado foi cadastrado, mas ainda
    // não começou a ser atendido.
    ABERTO,

    // A equipe iniciou o atendimento.
    EM_ATENDIMENTO,

    // O serviço foi finalizado.
    CONCLUIDO
}
