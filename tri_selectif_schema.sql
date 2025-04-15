CREATE DATABASE IF NOT EXISTS tri_selectif;
USE tri_selectif;

-- ============================
-- TABLE Adresse
-- ============================
CREATE TABLE Adresse (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL,
    nomRue VARCHAR(100) NOT NULL,
    codePostal INT NOT NULL CHECK (codePostal > 1000 AND codePostal < 100000),
    ville VARCHAR(100) NOT NULL,
    UNIQUE (numero, nomRue, codePostal, ville)  -- Empêche la duplication d'adresses exactes
);

-- ============================
-- TABLE Centre de tri
-- ============================
CREATE TABLE CentreTri (
    idCentre INT AUTO_INCREMENT PRIMARY KEY,
    nomCentre VARCHAR(100) NOT NULL,
    adresse_id INT NOT NULL,
    FOREIGN KEY (adresse_id) REFERENCES Adresse(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (nomCentre)  -- Assure qu'il n'y a pas de centre de tri avec le même nom
);

-- ============================
-- TABLE Bac
-- ============================
CREATE TABLE Bac (
    idBac CHAR(36) PRIMARY KEY,
    couleur ENUM('vert', 'jaune', 'bleu', 'gris', 'toutCol') NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),  -- Vérifie que la capacité est positive
    contenu INT NOT NULL,
    CHECK (contenu >= 0 AND contenu <= capacite),  -- Vérifie que le contenu est positif ou égal à 0
    centre_id INT NOT NULL,
    adresse_id INT NOT NULL,
    FOREIGN KEY (centre_id) REFERENCES CentreTri(idCentre) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (adresse_id) REFERENCES Adresse(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================
-- TABLE Menage
-- ============================
CREATE TABLE Menage (
    nomCompte VARCHAR(50) PRIMARY KEY,
    motDePasse VARCHAR(100) NOT NULL,
    pointsFidelite INT NOT NULL DEFAULT 0,  -- Points de fidélité, initialisés à 0 par défaut
    adresse_id INT NOT NULL,
    FOREIGN KEY (adresse_id) REFERENCES Adresse(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================
-- TABLE Commerce
-- ============================
CREATE TABLE Commerce (
    idCommerce CHAR(36) PRIMARY KEY,
    nomCommerce VARCHAR(100) NOT NULL,
    adresse_id INT NOT NULL,
    FOREIGN KEY (adresse_id) REFERENCES Adresse(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (nomCommerce)  -- Assure qu'il n'y a pas de commerce avec le même nom
);

-- ============================
-- TABLE ContratPartenariat
-- ============================
CREATE TABLE ContratPartenariat (
    idCentreP INT NOT NULL,
    idCommerceP CHAR(36) NOT NULL,
    estPartenaire BOOLEAN NOT NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    PRIMARY KEY (idCentreP, idCommerceP),
    FOREIGN KEY (idCentreP) REFERENCES CentreTri(idCentre) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (idCommerceP) REFERENCES Commerce(idCommerce) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================
-- TABLE BonReduction
-- ============================
CREATE TABLE BonReduction (
    idBon CHAR(36) PRIMARY KEY,
    valeur DOUBLE NOT NULL CHECK (valeur > 0),  -- Vérifie que la valeur du bon est positive
    bonUtilise BOOLEAN NOT NULL DEFAULT false,  -- Initialisation à false
    commerce_id CHAR(36) NOT NULL,
    menage_id VARCHAR(50) NOT NULL,
    dateExpiration DATE NOT NULL,
    FOREIGN KEY (commerce_id) REFERENCES Commerce(idCommerce) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (menage_id) REFERENCES Menage(nomCompte) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================
-- TABLE Depot
-- ============================
CREATE TABLE Depot (
    idDepot CHAR(36) PRIMARY KEY,
    poidsDepot INT NOT NULL CHECK (poidsDepot > 0),  -- Vérifie que le poids est positif ou nul
    couleur ENUM('vert', 'jaune', 'bleu', 'gris', 'toutCol') NOT NULL,
    typeDechet ENUM('verre', 'carton', 'plastique', 'metal', 'papier', 'autre', 'toutType') NOT NULL,
    resultat ENUM('correct', 'incorrect', 'total') NOT NULL,
    pointsGagnes INT NOT NULL DEFAULT 0,  -- Points gagnés initialisés à 0 par défaut
    dateDepot DATE NOT NULL,
    heureDepot TIME NOT NULL,
    adresse_id INT NOT NULL,
    menage_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (adresse_id) REFERENCES Adresse(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (menage_id) REFERENCES Menage(nomCompte) ON DELETE CASCADE ON UPDATE CASCADE
);

