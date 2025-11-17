                    Système de transfert de fichiers sécurisé
*Fonctionnalités principales :
Serveur multithread (un thread par client) : SecureFileServer et ClientTransferHandler, 
Protocole en 3 phases : authentification, négociation, transfert & vérification ,
Chiffrement AES pour protéger le contenu des fichiers, 
Hachage SHA-256 pour garantir l'intégrité, 
Interface client en ligne de commande (SecureFileClient).

*Protocole en 3 phases
1. Authentification (
Le client envoie login + mot de passe.
Le serveur répond :
AUTH_OK
AUTH_FAIL (et ferme)
)

3. Négociation (
Le client envoie : nom du fichier, taille, SHA-256.
Le serveur répond : READY_FOR_TRANSFER.)

4. Transfert sécurisé (
Le client envoie le fichier chiffré.
Le serveur le déchiffre, l’enregistre puis vérifie l’intégrité avec le SHA-256.
Réponse :
TRANSFER_SUCCESS
TRANSFER_FAIL )

*Sécurité
AES pour chiffrer/déchiffrer le fichier.
SHA-256 pour vérifier l’intégrité.
Une clé AES commune doit être partagée entre client et serveur.

*Exécution
Lancer le serveur :
java SecureFileServer <port>
Lancer le client :
java SecureFileClient <ip> <port> <login> <password> <fichier>

***Équipe du projet***
OUYAHIA Salma , SRIJA Fatima-Zahra


