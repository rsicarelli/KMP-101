import SwiftUI
import KotlinShared

struct ContentView: View {
    @State private var showText = false

    var body: some View {
        Button("Show Text") { showText.toggle() }
        if showText { Text(HelloWorld_appleKt.helloWorld()) }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

//struct ComposeView: UIViewControllerRepresentable {
//    func makeUIViewController(context: Context) -> UIViewController {
//        MainViewControllerKt.MainViewController()
//    }
//
//    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
//}
//
//struct ContentView: View {
//    var body: some View {
//        ComposeView()
//                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
//    }
//}



