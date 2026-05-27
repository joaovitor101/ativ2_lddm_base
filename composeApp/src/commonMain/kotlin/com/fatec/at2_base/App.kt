package com.fatec.at2_base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatec.at2_base.model.Valorant
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ValorantScreen()
        }
    }
}

@Composable
private fun ValorantScreen() {
    val scope = rememberCoroutineScope()
    var agents by remember { mutableStateOf<List<Valorant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var editingId by remember { mutableStateOf<Int?>(null) }

    var agent by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    fun clearForm() {
        editingId = null
        agent = ""
        description = ""
        country = ""
    }

    fun loadAgents() {
        scope.launch {
            isLoading = true
            feedback = null
            runCatching {
                ApiClient.getAgents()
            }.onSuccess {
                agents = it
            }.onFailure {
                feedback = "Erro ao carregar lista: ${it.message.orEmpty()}"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadAgents()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Agentes Valorant",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = agent,
                    onValueChange = { agent = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Agente") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descricao") }
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Pais") },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        enabled = !isSaving,
                        onClick = {
                            if (agent.isBlank() || description.isBlank() || country.isBlank()) {
                                feedback = "Preencha agente, descricao e pais."
                                return@Button
                            }

                            scope.launch {
                                isSaving = true
                                feedback = null
                                runCatching {
                                    val currentEditingId = editingId
                                    if (currentEditingId == null) {
                                        ApiClient.createAgent(
                                            agent = agent.trim(),
                                            description = description.trim(),
                                            country = country.trim()
                                        )
                                    } else {
                                        ApiClient.updateAgent(
                                            id = currentEditingId,
                                            valorant = Valorant(
                                                id = currentEditingId,
                                                agent = agent.trim(),
                                                description = description.trim(),
                                                country = country.trim()
                                            )
                                        )
                                    }
                                    ApiClient.getAgents()
                                }.onSuccess {
                                    feedback = if (editingId == null) {
                                        "Agente cadastrado com sucesso."
                                    } else {
                                        "Agente atualizado com sucesso."
                                    }
                                    clearForm()
                                    agents = it
                                }.onFailure {
                                    feedback = "Erro ao salvar: ${it.message.orEmpty()}"
                                }
                                isSaving = false
                            }
                        }
                    ) {
                        Text(
                            when {
                                isSaving -> "Salvando..."
                                editingId == null -> "Cadastrar"
                                else -> "Salvar"
                            }
                        )
                    }

                    if (editingId != null) {
                        OutlinedButton(
                            enabled = !isSaving,
                            onClick = {
                                clearForm()
                                feedback = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }

                    Button(
                        enabled = !isLoading,
                        onClick = { loadAgents() }
                    ) {
                        Text("Atualizar")
                    }
                }

                feedback?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Text(
                text = "Lista consumida do GET /valorant",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (isLoading) {
            item {
                CircularProgressIndicator()
            }
        }

        items(agents, key = { it.id }) { valorant ->
            AgentCard(
                valorant = valorant,
                onEdit = {
                    editingId = valorant.id
                    agent = valorant.agent
                    description = valorant.description.orEmpty()
                    country = valorant.country.orEmpty()
                    feedback = "Editando ${valorant.agent}"
                },
                onDelete = {
                    scope.launch {
                        isLoading = true
                        feedback = null
                        runCatching {
                            ApiClient.deleteAgent(valorant.id)
                            ApiClient.getAgents()
                        }.onSuccess {
                            if (editingId == valorant.id) {
                                clearForm()
                            }
                            agents = it
                            feedback = "Agente excluido com sucesso."
                        }.onFailure {
                            feedback = "Erro ao excluir: ${it.message.orEmpty()}"
                        }
                        isLoading = false
                    }
                }
            )
        }
    }
}

@Composable
private fun AgentCard(
    valorant: Valorant,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${valorant.id} - ${valorant.agent}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = valorant.description.orEmpty())
            Text(
                text = valorant.country.orEmpty(),
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onDelete) {
                    Text("Excluir")
                }
            }
        }
    }
}
