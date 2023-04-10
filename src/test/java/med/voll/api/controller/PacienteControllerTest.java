package med.voll.api.controller;

import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.endereco.Endereco;
import med.voll.api.paciente.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PacienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private JacksonTester<Page<DadosListagemPaciente>> dadosListagemPacienteJson;

    @Autowired
    private JacksonTester<DadosCadastroPaciente> dadosCadastroPacienteJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoPaciente> dadosDetalhamentoPacienteJson;

    @MockBean
    private PacienteRepository repository;

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 informacoes estao invalidas")
    @WithMockUser
    void cadastrar_cenario1() throws Exception {
        var response = mvc
                .perform(post("/pacientes")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 quando informacoes estao validas")
    @WithMockUser
    void cadastrar_cenario2() throws Exception{
        var dadosCadastro = new DadosCadastroPaciente(
                "Felipe",
                "felipe@vollmed.com.br",
                "11978944652",
                "191.555.456-10",
                dadosEndereco());

        when(repository.save(any())).thenReturn(new Paciente(dadosCadastro));

        var response = mvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dadosCadastroPacienteJson.write(dadosCadastro).getJson()))
                .andReturn().getResponse();

        var dadosDetalhamento = new DadosDetalhamentoPaciente(
                null,
                dadosCadastro.nome(),
                dadosCadastro.email(),
                dadosCadastro.cpf(),
                dadosCadastro.telefone(),
               new Endereco(dadosCadastro.endereco()));

        var jsonEsperado = dadosDetalhamentoPacienteJson.write(dadosDetalhamento).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "rua java",
                "Vila Java",
                "02866158",
                "Sao Paulo",
                "SP",
                "100",
                "casa"
        );
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 informacoes estao invalidas")
    @WithMockUser
    void listar_cenario1() throws Exception {
        var response = mvc
                .perform(post("/pacientes")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 quando informacoes sao validas ")
    @WithMockUser
    void listar_cenario2() throws Exception{
        var dadosListagemPaciente = new DadosListagemPaciente(
                1L,
                "Felipe",
                "felipe@vollmed.com.br",
                "191.555.456-10");

        when(repository.findAllByAtivoTrue(any())).thenReturn(page());

        var response = mvc.perform(get("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(dadosListagemPacienteJson.write(pageDadosListagemPaciente())))).andReturn().getResponse();

        var dadosDetalhamento = new DadosDetalhamentoPaciente(
                dadosListagemPaciente.id(),
                dadosListagemPaciente.nome(),
                dadosListagemPaciente.email(),
                dadosListagemPaciente.cpf(),
                null,
                null);

        var jsonEsperado = dadosDetalhamentoPacienteJson.write(dadosDetalhamento).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(response.getContentType());
        assertNotNull(jsonEsperado);
}

    private Page<Paciente> page() {
        return Page.empty(PageRequest.of(0,10));
    }

    private Page<DadosListagemPaciente> pageDadosListagemPaciente() {
        return Page.empty(PageRequest.of(0,10));
    }
}