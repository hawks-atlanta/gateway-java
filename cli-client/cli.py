#!/usr/bin/env python3

# Simple SOAP client to test the service
# Install Zeep SOAP client https://docs.python-zeep.org/
# $ pip install zeep

import handlers

available_features = [
    {"name": "🧪 All (testing)", "handler": handlers.runWithHardcodedValuesHandler},
    {"name": "👥 Register default user", "handler": handlers.registerDefaultUserHandler},
    {"name": "👥 Register", "handler": handlers.registerHandler},
    {"name": "🔑 Login", "handler": handlers.loginHandler},
    {"name": "📄 Upload file", "handler": handlers.uploadHandler},
    {"name": "📄 Download file", "handler": handlers.downloadHandler},
    {"name": "🚪 Finish program", "handler": handlers.exitHandler},
]


def show_menu():
    # Print menu
    for i in range(len(available_features)):
        feature = available_features[i]
        print(f"{i + 1}. {feature['name']}")

    # Get menu selection and call handler
    selection = get_menu_selection()
    available_features[selection - 1]["handler"]()

    # Show menu again
    print("\n")
    show_menu()


def get_menu_selection():
    # Get menu selection
    while True:
        try:
            selection = int(input("Select: "))
            if selection > 0 and selection <= len(available_features):
                return selection
            else:
                print("Invalid selection")
        except:
            print("Invalid selection")


if __name__ == "__main__":
    show_menu()
