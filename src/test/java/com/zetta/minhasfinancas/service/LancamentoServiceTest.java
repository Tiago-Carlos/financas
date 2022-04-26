package com.zetta.minhasfinancas.service;

import com.zetta.minhasfinancas.exception.RegraNegocioException;
import com.zetta.minhasfinancas.model.entity.Lancamento;
import com.zetta.minhasfinancas.model.entity.Usuario;
import com.zetta.minhasfinancas.model.enums.StatusLancamento;
import com.zetta.minhasfinancas.model.repository.LancamentoRepository;
import com.zetta.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.zetta.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    /**
     * Mockito.doNothing: O que quero testar é o salvar lançamento.
     * Porem, aqui o salvar lançamento:
     * -> validar(lancamento);
     * lancamento.setStatus(StatusLancamento.PENDENTE);
     * return repository.save(lancamento);
     * Esse validar ai não está sendo testado, logo, deve ser ignorado, e
     * é isso que o mockito faz.
     */
    @Test
    public void deveSalvarUmLancamento() {
        Lancamento salvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(salvar);

        Lancamento salvo = LancamentoRepositoryTest.criarLancamento();
        salvo.setId(1l);
        salvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(salvar)).thenReturn(salvo);

        Lancamento lancamento = service.salvar(salvar);

        assertThat( lancamento.getId() ).isEqualTo(salvo.getId());
        assertThat( lancamento.getStatus() ).isEqualTo( salvo.getStatus());
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento salvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(salvar);

        Assertions.catchThrowableOfType( () -> service.salvar(salvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(salvar);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento salvo = LancamentoRepositoryTest.criarLancamento();
        salvo.setId(1l);
        salvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(salvo);

        Mockito.when(repository.save(salvo)).thenReturn(salvo);

        Lancamento lancamento = service.atualizar(salvo);

        Mockito.verify(repository, Mockito.times(1)).save(salvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento salvar = LancamentoRepositoryTest.criarLancamento();

        catchThrowableOfType(() -> service.atualizar(salvar), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(salvar);
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        service.deletar(lancamento);

        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoDeletarUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);

        Mockito.verify( repository, Mockito.never() ).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when( repository.findAll(Mockito.any(Example.class)) ).thenReturn(lista);

        List<Lancamento> result = service.buscar(lancamento);

        assertThat(result).isNotEmpty()
                .hasSize(1)
                .contains(lancamento);
    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novo = StatusLancamento.EFETIVADO;

        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novo);

        assertThat(lancamento.getStatus()).isEqualTo(novo);
        Mockito.verify(service).atualizar(lancamento);

    }

    @Test
    public void deveObterUmLancamentoPorId() {
        long id = 1l;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> pego = service.obterPorId(id);

        assertThat(pego.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoNaoExisteLancamento() {
        long id = 1l;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Lancamento> pego = service.obterPorId(id);

        assertThat(pego.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErroAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("Descrição");

        erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(1);

        erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(2022);

        erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        lancamento.setUsuario(new Usuario());
        lancamento.getUsuario().setId(1l);

        erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.valueOf(90));

        erro = catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");

    }
}
