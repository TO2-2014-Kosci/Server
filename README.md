Moduł Server
======================
Moduł serwera i systemu komunikacji między użytkownikami a grą, stworzony w ramach zajęć kursu Technologii Obiektowych 2 na AGH.

Do poprawnego działania moduł wymaga uruchomionego i skonfigurowanego serwera RabbitMQ. Poniżej podano komendy konfigurujące serwer kolejek w Linuksach.
Konfiguracja w Windowsie wygląda bardzo podobnie, z dokładnością do sposobu uruchamiania narzędzia rabbitmqctl

    sudo rabbitmqctl add_vhost gui_host
    sudo rabbitmqctl add_user dice_server TheySeeMeRerollin
    sudo rabbitmqctl add_user gui_user TO2rbenvawesome
    sudo rabbitmqctl set_permissions -p gui_host dice_server ".*" ".*" ".*"
    sudo rabbitmqctl set_permissions -p gui_host gui_user ".*" ".*" ".*"

Oprócz tego, moduł korzysta z innych modułów Gry w Kości:

    Interfaces
    GameControllers
    AI

Oraz zewnętrznych bibliotek:

    org.json
    rabbitmq-client
