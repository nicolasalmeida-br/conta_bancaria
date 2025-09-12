document.addEventListener("DOMContentLoaded", () => {
    const formCliente = document.getElementById("clienteForm");
    const nomeInput = document.getElementById("nome");
    const cpfInput = document.getElementById("cpf");
    const tipoContaInput = document.getElementById("tipoConta");
    const saldoInput = document.getElementById("saldo");
    const statusInput = document.getElementById("statusConta");
    const listaClientes = document.getElementById("clienteCards");
    const searchInput = document.getElementById("searchInput");
    const sortNomeBtn = document.getElementById("sortNome");
    const sortSaldoBtn = document.getElementById("sortSaldo");

    let clientes = JSON.parse(localStorage.getItem("clientes") || "[]");
    let sortNomeAsc = true;
    let sortSaldoAsc = true;
    let editandoClienteId = null;

    function salvarLocalStorage() {
        localStorage.setItem("clientes", JSON.stringify(clientes));
    }

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
                <p>Saldo: <span class="${cliente.saldo >= 1000 ? "saldo-alto" : "saldo-baixo"}">R$ ${cliente.saldo.toFixed(2)}</span></p>
                <p>Status: ${cliente.status}</p>
                <div class="actions">
                    <button onclick="editarCliente(${cliente.id})">Editar</button>
                    <button class="danger" onclick="excluirCliente(${cliente.id})">Excluir</button>
                </div>
            `;
            listaClientes.appendChild(div);
        });
    }

    function limparFormulario() {
        formCliente.reset();
        document.getElementById("btnSubmit").textContent = "Cadastrar";
        document.getElementById("btnCancelar").classList.add("hidden");
        editandoClienteId = null;
    }

    // Cadastrar ou salvar alterações
    formCliente.addEventListener("submit", (e) => {
        e.preventDefault();
        const clienteData = {
            id: editandoClienteId ?? Date.now(),
            nome: nomeInput.value.trim(),
            cpf: cpfInput.value.trim(),
            tipoConta: tipoContaInput.value,
            saldo: parseFloat(saldoInput.value) || 0,
            status: statusInput.value
        };

        if (editandoClienteId) {
            clientes = clientes.map(c => c.id === editandoClienteId ? clienteData : c);
        } else {
            clientes.push(clienteData);
        }

        salvarLocalStorage();
        renderClientes(clientes);
        limparFormulario();
    });

    // Cancelar edição
    document.getElementById("btnCancelar").addEventListener("click", () => {
        limparFormulario();
    });

    // Editar cliente
    window.editarCliente = function(id) {
        const c = clientes.find(cli => cli.id === id);
        if (!c) return;
        nomeInput.value = c.nome;
        cpfInput.value = c.cpf;
        tipoContaInput.value = c.tipoConta;
        saldoInput.value = c.saldo;
        statusInput.value = c.status;

        document.getElementById("btnSubmit").textContent = "Salvar Alterações";
        document.getElementById("btnCancelar").classList.remove("hidden");
        editandoClienteId = id;
    };

    // Excluir cliente
    window.excluirCliente = function(id) {
        clientes = clientes.filter(c => c.id !== id);
        salvarLocalStorage();
        renderClientes(clientes);
    };

    // Ordenar por nome
    sortNomeBtn.addEventListener("click", () => {
        clientes.sort((a,b) => sortNomeAsc ? a.nome.localeCompare(b.nome) : b.nome.localeCompare(a.nome));
        sortNomeAsc = !sortNomeAsc;
        renderClientes(clientes);
    });

    // Ordenar por saldo
    sortSaldoBtn.addEventListener("click", () => {
        clientes.sort((a,b) => sortSaldoAsc ? a.saldo - b.saldo : b.saldo - a.saldo);
        sortSaldoAsc = !sortSaldoAsc;
        renderClientes(clientes);
    });

    // Pesquisa
    searchInput.addEventListener("input", () => {
        const term = searchInput.value.toLowerCase();
        renderClientes(clientes.filter(c => c.nome.toLowerCase().includes(term) || c.cpf.includes(term)));
    });

    // Render inicial
    renderClientes(clientes);
});