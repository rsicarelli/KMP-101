## Como o Kotlin/Native exporta código para o Swift

No último post, aprendemos como utilizar código Kotlin no Swift.
Aprendemos sobre algumas técnicas para melhorar o codigo exportado para o Swift,
e como as anotações `@HiddenFromObjC` controla a visibilidade do código no Swift.

Nesse post, vamos aprofundar sobre como essa exportação funciona e o impacto no nosso código gerado.

## Recapitulando a exportação de código
Ao compilar um `.framework` com o Kotlin/Native, o compilador gera uma série de arquivos, sendo eles:

- `Headers/KotlinShared.h`: Este arquivo é a interface gerada automaticamente pelo KMP que expõe as funções e classes Kotlin para o ambiente Objective-C/Swift. Ele serve como a ponte principal, convertendo tipos Kotlin em tipos equivalentes nativos para o iOS. Por exemplo, `String` do Kotlin é mapeado para `NSString`, e coleções como `List` são mapeadas para `NSArray`.
- `KotlinShared.c` (ou sem extensão): Este é o arquivo binário compilado que contém as implementações nativas do código Kotlin, traduzido para [LLVM IR](https://mcyoung.xyz/2023/08/01/llvm-ir/). Ele é consumido diretamente pelo iOS como parte da framework.
- Outros componentes (como `.plist` e `bundles`): Esses arquivos são informações adicionais necessárias para o funcionamento do framework no iOS, como metadados, recursos compartilhados e configurações específicas.

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-native-xcframework-expanded.png?raw=true" width="200" />