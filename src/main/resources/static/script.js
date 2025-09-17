document.addEventListener("DOMContentLoaded", () => {
    const formCliente = document.getElementById("clienteForm");
    const nomeInput = document.getElementById("nome");
    const cpfInput = document.getElementById("cpf");
    const tipoContaInput = document.getElementById("tipoConta");
    const saldoInput = document.getElementById("saldo");
    const statusInput = document.getElementById("statusConta");
    const limiteInput = document.getElementById("limite");
    const taxaInput = document.getElementById("taxa");
    const rendimentoInput = document.getElementById("rendimento");
    const correnteFields = document.getElementById("correnteFields");
    const poupancaFields = document.getElementById("poupancaFields");
    const listaClientes = document.getElementById("clienteCards");
    const searchInput = document.getElementById("searchInput");
    const sortNomeBtn = document.getElementById("sortNome");
    const sortSaldoBtn = document.getElementById("sortSaldo");

    let clientes = [];
    let sortNomeAsc = true;
    let sortSaldoAsc = true;
    let editandoClienteId = null;

    tipoContaInput.addEventListener("change", () => {
        if (tipoContaInput.value === "Corrente") {
            correnteFields.style.display = "";
            poupancaFields.style.display = "none";
        } else if (tipoContaInput.value === "Poupanca") {
            correnteFields.style.display = "none";
            poupancaFields.style.display = "";
        } else {
            correnteFields.style.display = "none";
            poupancaFields.style.display = "none";
        }
    });

    function formatarCPF(cpf) {
        cpf = cpf.toString().padStart(11, '0');
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    }

    function renderClientes(lista) {
        listaClientes.innerHTML = "";
        lista.forEach(cliente => {
            const div = document.createElement("div");
            div.classList.add("card");
            div.innerHTML = `
                <h3>${cliente.nome}</h3>
                <p>CPF: ${formatarCPF(cliente.cpf)}</p>
                <p>Tipo de Conta: ${cliente.tipoConta}</p>
                <p>Saldo: <span class="${cliente.saldo >= 1000 ? "saldo-alto" : "saldo-baixo"}">R$ ${Number(cliente.saldo).toFixed(2)}</span></p>
                <p>Status: ${cliente.status}</p>
                <div class="actions">
                    <button onclick="editarCliente('${cliente.numero}')">Editar</button>
                    <button class="danger" onclick="excluirCliente('${cliente.numero}')">Excluir</button>
                </div>
            `;
            listaClientes.appendChild(div);
        });
    }

    function limparFormulario() {
        formCliente.reset();
        document.getElementById("btnSubmit").textContent = "Cadastrar";
        document.getElementById("btnCancelar").classList.add("hidden");
        correnteFields.style.display = "none";
        poupancaFields.style.display = "none";
        editandoClienteId = null;
    }

    async function carregarClientes() {
        const [corrente, poupanca] = await Promise.all([
            fetch("/contas/corrente").then(r => r.json()).catch(() => []),
            fetch("/contas/poupanca").then(r => r.json()).catch(() => [])
        ]);
        clientes = [
            ...corrente.map(c => ({ ...c, tipoConta: "Corrente" })),
            ...poupanca.map(p => ({ ...p, tipoConta: "Poupanca" }))
        ];
        renderClientes(clientes);
    }

    // Cadastrar ou salvar alterações
    formCliente.addEventListener("submit", async (e) => {
        e.preventDefault();
        const tipo = tipoContaInput.value;
        let dto, url, method = "POST";
        if (tipo === "Corrente") {
            dto = {
                numero: editandoClienteId || undefined,
                nome: nomeInput.value,
                cpf: cpfInput.value,
                status: statusInput.value,
                saldo: saldoInput.value,
                limite: limiteInput.value,
                taxa: taxaInput.value
            };
            url = "/contas/corrente";
        } else if (tipo === "Poupanca") {
            dto = {
                numero: editandoClienteId || undefined,
                nome: nomeInput.value,
                cpf: cpfInput.value,
                status: statusInput.value,
                saldo: saldoInput.value,
                rendimento: rendimentoInput.value
            };
            url = "/contas/poupanca";
        } else {
            alert("Selecione o tipo de conta.");
            return;
        }

        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        await carregarClientes();
        limparFormulario();
    });

    // Cancelar edição
    document.getElementById("btnCancelar").addEventListener("click", () => {
        limparFormulario();
    });

    // Editar cliente
    window.editarCliente = async function(numero) {
        let cliente = clientes.find(cli => cli.numero === numero);
        if (!cliente) return;
        nomeInput.value = cliente.nome || "";
        cpfInput.value = cliente.cpf || "";
        tipoContaInput.value = cliente.tipoConta;
        saldoInput.value = cliente.saldo;
        statusInput.value = cliente.status || "";

        if (cliente.tipoConta === "Corrente") {
            limiteInput.value = cliente.limite;
            taxaInput.value = cliente.taxa;
            correnteFields.style.display = "";
            poupancaFields.style.display = "none";
        } else if (cliente.tipoConta === "Poupanca") {
            rendimentoInput.value = cliente.rendimento;
            correnteFields.style.display = "none";
            poupancaFields.style.display = "";
        }
        document.getElementById("btnSubmit").textContent = "Salvar Alterações";
        document.getElementById("btnCancelar").classList.remove("hidden");
        editandoClienteId = numero;
    };

    // Excluir cliente
    window.excluirCliente = async function(numero) {
        let tipo = clientes.find(c => c.numero === numero)?.tipoConta;
        if (!tipo) return;
        let url = tipo === "Corrente" ? `/contas/corrente/${numero}` : `/contas/poupanca/${numero}`;
        await fetch(url, { method: "DELETE" });
        await carregarClientes();
    };

    // Ordenar por nome
    sortNomeBtn.addEventListener("click", () => {
        clientes.sort((a, b) => sortNomeAsc ? a.nome.localeCompare(b.nome) : b.nome.localeCompare(a.nome));
        sortNomeAsc = !sortNomeAsc;
        renderClientes(clientes);
    });

    // Ordenar por saldo
    sortSaldoBtn.addEventListener("click", () => {
        clientes.sort((a, b) => sortSaldoAsc ? a.saldo - b.saldo : b.saldo - a.saldo);
        sortSaldoAsc = !sortSaldoAsc;
        renderClientes(clientes);
    });

    // Pesquisa
    searchInput.addEventListener("input", () => {
        const term = searchInput.value.toLowerCase();
        renderClientes(clientes.filter(c => (c.nome || "").toLowerCase().includes(term) || (c.cpf || "").includes(term)));
    });

    // Render inicial
    carregarClientes();
});