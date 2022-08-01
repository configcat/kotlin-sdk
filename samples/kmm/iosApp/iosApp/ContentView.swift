import SwiftUI
import shared

struct ContentView: View {
    let flags = FeatureFlags()
    
    @State var text = "loading..."
    
	var body: some View {
        Text("Hello from " + Platform().platform + "!")
        
        Text(text).onAppear() {
            flags.isFeatureEnabled(key: "isPOCFeatureEnabled", email: "configcat@example.com") { result, error in
                self.text = "isPOCFeatureEnabled: " + String(result?.boolValue ?? false)
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
