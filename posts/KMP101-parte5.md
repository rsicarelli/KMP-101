Nos últimos artigos, focamos em diversos conceitos-chave do KMP e entendemos desde o paradigma multiplataforma, até a configuração do ambiente.

Dessa vez, vamos colocar a mão na massa efetivamente, e criar nosso primeiro "Olá mundo" em Android, iOS e Desktop!

---

## Primeiro passo: formas de criar projetos

Existem diversas maneiras para criarmos um novo projeto KMP, como, por exemplo:

1. [KMP Wizard](https://kmp.jetbrains.com/): Essa ferramenta web surgiu em novembro de 2023. Muito promissor, já que a JetBrains pretende complementar essa ferramenta com modelos e outros alvos.
2. Templates do IntelliJ: Tanto do Android Studio (com plugin KMP) quanto do IntelliJ oferecem projetos modelos, através do "Arquivo > Novo > Projeto".
3. Manual: opção para quem já domina e tem compreensão mais profunda sobre o KMP, e adotam estratégias específicas, como a utilização do `build-logic` (veja meu artigo sobre esse tópico [Android Plataforma - Parte 3: Compartilhando scripts do Gradle](https://dev.to/rsicarelli/android-plataforma-parte-3-compartilhando-scripts-do-gradle-5ak3))

### Criando um projeto utilizando o KMP Wizard

Já que iremos utilizar o **Fleet** para esse e os próximos artigos, o [KMP Wizard](https://kmp.jetbrains.com/) é uma opção perfeita, já que o Fleet ainda não possui um mecanismo de templates.

O uso do Wizard é super intuitivo, caso precise de um passo a passo:
1. Atribua um nome e um ID de projeto
2. Selecione Android, iOS e Desktop
3. Clique em ***Download***;

> E a Web? O motivo merece um artigo separado sobre Kotlin/JS e Kotlin/Wasm.
>
> Em resumo, a versão "dos sonhos" do KMP para web ainda está em fase experimental, e o time do JetBrains optou por não incluir esse alvo por hora.

### Importando o novo projeto no Fleet

Utilizar o projeto gerado pelo KMP Wizard no **Fleet** é bem simples:

1. Extraia o `.zip` em alguma pasta no seu ambiente
2. Abra o **Fleet** e selecione `Open File or Folder...`
3. Selecione toda a pasta com o nome do seu projeto
4. Uma tela "Trust and Open Folder in Smart Mode" irá aparecer. Clique em ***trust***
5. Aguarde alguns instantes enquanto o **Fleet** inicia seu projeto. Você poderá ver o progresso no topo no canto direito

#### O que é o Smart Mode no Fleet?

Essa funcionalidade nos permite usar o **Fleet** tanto como um editor de texto leve quanto como um IDE completo, especialmente projetado para economizar recursos do sistema, habilitando recursos pesados do IDE somente quando necessário.

Sendo representado por um ícone de raio ⚡️ no topo direito do **Fleet**, o Smart Mode é essencial para diversas funcionalidades, incluindo:

- Realce semântico
- Auto-complete
- Refatoração
- Navegação e busca
- Busca por referências de uso

### Testando nas plataformas

Se tudo deu certo, o **Fleet** realizou as seguintes etapas
1. Inicializou o projeto
2. Identificou que temos 3 alvos:
   1. Android com o ícone do robo verde
   2. iOS com o ícone da fruta mordida
   3. Desktop com o ícone do elefante (do Gradle)
3. Configurou cada alvo, e o tornou disponível na janela "Run & Debug" (clicando no ícone ▶️ ***run & debug***)

![Executando o projeto no Fleet](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/fleet-hello-world-run.png?raw=true)

Agora é só testar! Clique em cada uma das plataformas, e execute nosso Olá Mundo:

![Demo em todas as plataformas](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/hello-world-kmp-compose.gif?raw=true)

## Conclusão
Simples, não é? De fato, foram anos de desenvolvimento para que o Kotlin Multiplataforma alcançasse este estágio. Atualmente, a comunidade está entusiasmada com a facilidade de criar projetos multiplataforma nativos para Android, iOS e Desktop em apenas alguns minutos.

Agora que temos um projeto base configurado e funcionando, estamos prontos para avançar em tópicos mais específicos do KMP. 

No próximo artigo, exploraremos um aspecto crucial do Kotlin Multiplataforma: a integração com o Gradle através do Plugin KMP.

Até lá!



---

## Feedbacks

🔗 [Nova issue no repositório KMP-101](https://github.com/rsicarelli/KMP101/issues/new/choose)

Sua opinião e contribuição fazem desse conteúdo uma fonte de aprendizado mais completo para todo mundo!

Qualquer dúvida, crítica ou sugestão podem ser feitas no repositório [KMP-101](https://github.com/rsicarelli/KMP101)

---

> 🤖 Artigo foi escrito com o auxílio do ChatGPT 4, utilizando o plugin Web.
>
> As fontes e o conteúdo são revisados para garantir a relevância das informações fornecidas, assim como as fontes utilizadas em cada prompt.
>
> No entanto, caso encontre alguma informação incorreta ou acredite que algum crédito está faltando, por favor, entre em contato!

---

> Referências
> - [JetBrains Fleet Documentation](https://www.jetbrains.com/help/fleet/smart-mode.html) 

