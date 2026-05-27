package com.fatec.at2_base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fatec.at2_base.model.Valorant
import kotlinx.coroutines.launch

private val ValorantColorScheme = lightColorScheme(
    primary = Color(0xFFE63946),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD8),
    onPrimaryContainer = Color(0xFF410003),
    secondary = Color(0xFF006D77),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB6EAEE),
    onSecondaryContainer = Color(0xFF002023),
    tertiary = Color(0xFFFFB703),
    onTertiary = Color(0xFF2A1C00),
    tertiaryContainer = Color(0xFFFFE7A6),
    onTertiaryContainer = Color(0xFF2A1C00),
    background = Color(0xFFFFF8F1),
    onBackground = Color(0xFF1F1A17),
    surface = Color(0xFFFFFBF7),
    onSurface = Color(0xFF1F1A17),
    surfaceVariant = Color(0xFFEDE2D8),
    onSurfaceVariant = Color(0xFF51443C),
    outline = Color(0xFF83736A),
    outlineVariant = Color(0xFFD7C6BA)
)

private val ScreenShape = RoundedCornerShape(8.dp)

@Composable
fun App() {
    MaterialTheme(colorScheme = ValorantColorScheme) {
        ValorantScreen()
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8F1),
                        Color(0xFFEAF7F4),
                        Color(0xFFFFECE8)
                    )
                )
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1A17), ScreenShape)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Agentes Valorant",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Cadastro dos personagens e suas origens",
                    color = Color(0xFFFFDAD8),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, ScreenShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, ScreenShape)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (editingId == null) "Novo agente" else "Editando agente",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
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
                    label = { Text("Descricao") },
                    minLines = 3
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Pais") },
                    singleLine = true
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    shape = ScreenShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (editingId != null) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving,
                            shape = ScreenShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            onClick = {
                                clearForm()
                                feedback = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        shape = ScreenShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = { loadAgents() }
                    ) {
                        Text("Atualizar")
                    }
                }

                feedback?.let {
                    FeedbackMessage(message = it)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lista de agentes",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${agents.size} registro(s)",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
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
private fun FeedbackMessage(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, ScreenShape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun AgentCard(
    valorant: Valorant,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ScreenShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valorant.agent,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${valorant.id}",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = valorant.description.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = valorant.country.orEmpty(),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shape = ScreenShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = onEdit
                ) {
                    Text("Editar")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shape = ScreenShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onDelete
                ) {
                    Text("Excluir")
                }
            }
        }
    }
}
