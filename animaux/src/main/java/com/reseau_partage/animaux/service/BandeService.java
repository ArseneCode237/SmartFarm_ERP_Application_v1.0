package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bande.BandeRequest;
import com.reseau_partage.animaux.dto.bande.BandeResponse;
import com.reseau_partage.animaux.exception.IncompatibiliteEspeceException;
import com.reseau_partage.animaux.exception.QuantiteInvalideException;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.animaux.mapper.BandeMapper;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Enclos;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.Site;
import com.reseau_partage.core.entities.StatutBande;
import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.FermeRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.SiteRepository;
import com.reseau_partage.core.repository.StructureRepository;

@Service
public class BandeService {

    private final BandeRepository bandeRepository;
    private final AnimalRepository animalRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final StructureRepository structureRepository;
    private final SiteRepository siteRepository;
    private final FermeRepository fermeRepository;
    private final BandeMapper bandeMapper;

    public BandeService(BandeRepository bandeRepository, AnimalRepository animalRepository, MouvementAnimalRepository mouvementRepository, StructureRepository structureRepository, SiteRepository siteRepository, FermeRepository fermeRepository, BandeMapper bandeMapper) {
        this.bandeRepository = bandeRepository;
        this.animalRepository = animalRepository;
        this.mouvementRepository = mouvementRepository;
        this.structureRepository = structureRepository;
        this.siteRepository = siteRepository;
        this.fermeRepository = fermeRepository;
        this.bandeMapper = bandeMapper;
    }

    @Transactional
    public BandeResponse create(BandeRequest request) {
        Structure structure = structureRepository.findById(request.structureId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));
        if (structure.getStatut() != StatutStructure.ACTIF) {
            throw new IllegalArgumentException("La structure doit etre active.");
        }
        if (structure instanceof Enclos e && e.getEspecesCompatibles() != null && !e.getEspecesCompatibles().contains(request.espece().name())) {
            throw new IncompatibiliteEspeceException("Structure incompatible avec l'espece " + request.espece());
        }
        Bande bande = bandeMapper.toEntity(request);
        bande.setStructure(structure);
        
        if (request.siteId() != null) {
            Site site = siteRepository.findById(request.siteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site", request.siteId()));
            bande.setSite(site);
        }
        
        // Initialiser les champs supplémentaires
        bande.setCategorie(request.categorie());
        bande.setDescription(request.description());
        bande.setProvenance(request.provenance());
        bande.setFournisseurNom(request.fournisseurNom());
        bande.setCoutAchatUnitaire(request.coutAchatUnitaire());
        bande.setRationJournaliereKg(request.rationJournaliereKg());
        // Initialiser les effectifs (effectifActuel est déjà initialisé par le mapper à effectifInitial)
        bande.setEffectifMorts(0);
        bande.setEffectifVendus(0);
        bande.setEffectifReformes(0);
        
        bande.setCodeBande(genererCodeBande(request));
        bande.setStatut(request.statut() != null ? request.statut() : StatutBande.EN_COURS);
        bandeRepository.save(bande);
        enregistrerMouvementEntree(bande, request.effectifInitial());
        return toResponse(bande);
    }

    @Transactional(readOnly = true)
    public Page<BandeResponse> list(Long structureId, Long siteId, Long fermeId, Espece espece, StatutBande statut, Pageable pageable) {
        return bandeRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public BandeResponse get(Long id) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        return toResponse(bande);
    }

    @Transactional
    public BandeResponse update(Long id, BandeRequest request) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        bande.setNom(request.nom());
        bande.setEspece(request.espece());
        bande.setRace(request.race());
        bande.setSouche(request.souche());
        bande.setCategorie(request.categorie());
        bande.setTypeProduction(request.typeProduction());
        bande.setDescription(request.description());
        bande.setNotes(request.notes());
        bande.setDateEntree(request.dateEntree());
        bande.setDateSortiePrevue(request.dateSortiePrevue());
        bande.setDateSortieReelle(request.dateSortieReelle());
        bande.setEffectifInitial(request.effectifInitial());
        // Mettre à jour effectifActuel seulement si fourni, sinon ne pas écraser
        if (request.effectifActuel() != null) {
            bande.setEffectifActuel(request.effectifActuel());
        }
        bande.setEffectifMorts(request.effectifMorts());
        bande.setEffectifVendus(request.effectifVendus());
        bande.setEffectifReformes(request.effectifReformes());
        bande.setPoidsMoyenEntreeKg(request.poidsMoyenEntreeKg());
        bande.setPoidsMoyenActuelKg(request.poidsMoyenActuelKg());
        bande.setPoidsTotalSortie(request.poidsTotalSortie());
        bande.setRationJournaliereKg(request.rationJournaliereKg());
        bande.setFcrCumule(request.fcrCumule());
        bande.setTauxPontePct(request.tauxPontePct());
        bande.setGainMoyenQuotidienG(request.gainMoyenQuotidienG());
        bande.setProvenance(request.provenance());
        bande.setFournisseurNom(request.fournisseurNom());
        bande.setCoutAchatUnitaire(request.coutAchatUnitaire());
        // Mettre à jour le statut seulement si fourni
        if (request.statut() != null) {
            bande.setStatut(request.statut());
        }
        
        if (request.siteId() != null && !Objects.equals(request.siteId(), bande.getSite() != null ? bande.getSite().getId() : null)) {
            var site = siteRepository.findById(request.siteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site", request.siteId()));
            bande.setSite(site);
        }
        
        if (request.structureId() != null && !Objects.equals(request.structureId(), bande.getStructure().getId())) {
            Structure destination = structureRepository.findById(request.structureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));
            if (destination.getStatut() != StatutStructure.ACTIF) {
                throw new IllegalArgumentException("La structure doit etre active.");
            }
            if (destination instanceof Enclos e && e.getEspecesCompatibles() != null && !e.getEspecesCompatibles().contains(request.espece().name())) {
                throw new IncompatibiliteEspeceException("Structure incompatible avec l'espece " + request.espece());
            }
            bande.setStructure(destination);
            enregistrerMouvementTransfert(bande, destination);
        }
        return toResponse(bande);
    }

    @Transactional
    public void sortieCollective(Long id, TypeMouvement typeMouvement, LocalDate dateSortie, Integer quantite, BigDecimal poidsKg, BigDecimal prixUnitaire, String motif, String operateurNom) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        if (bande.getStatut() != StatutBande.EN_COURS) {
            throw new IllegalArgumentException("La bande doit etre en cours.");
        }
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre superieure a 0.");
        }
        if (quantite > bande.getEffectifActuel()) {
            throw new QuantiteInvalideException(quantite, bande.getEffectifActuel());
        }
        bande.setEffectifActuel(bande.getEffectifActuel() - quantite);
        switch (typeMouvement) {
            case SORTIE_MORT -> {
                bande.setStatut(StatutBande.TERMINEE);
                bande.setEffectifMorts((bande.getEffectifMorts() == null ? 0 : bande.getEffectifMorts()) + quantite);
            }
            case SORTIE_VENTE -> {
                if (bande.getEffectifActuel() == 0) bande.setStatut(StatutBande.VENDUE);
                bande.setEffectifVendus((bande.getEffectifVendus() == null ? 0 : bande.getEffectifVendus()) + quantite);
            }
            case SORTIE_REFORME -> {
                if (bande.getEffectifActuel() == 0) bande.setStatut(StatutBande.TERMINEE);
                bande.setEffectifReformes((bande.getEffectifReformes() == null ? 0 : bande.getEffectifReformes()) + quantite);
            }
            default -> throw new IllegalArgumentException("Type de mouvement invalide pour une sortie collective.");
        }
        if (bande.getEffectifActuel() == 0) {
            bande.setDateSortieReelle(dateSortie);
        }
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setBande(bande);
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setDateMouvement(dateSortie);
        mouvement.setQuantite(quantite);
        mouvement.setStructureOrigine(bande.getStructure());
        mouvement.setPoidsKg(poidsKg);
        mouvement.setPrixUnitaire(prixUnitaire);
        mouvement.setMotif(motif);
        mouvement.setOperateurNom(operateurNom);
        mouvementRepository.save(mouvement);
    }

    @Transactional
    public void transfert(Long id, Long structureDestinationId, String operateurNom, String motif) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        if (bande.getStatut() != StatutBande.EN_COURS) {
            throw new IllegalArgumentException("La bande doit etre en cours.");
        }
        Structure destination = structureRepository.findById(structureDestinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", structureDestinationId));
        bande.setStructure(destination);
        enregistrerMouvementTransfert(bande, destination);
    }

    @Transactional
    public BandeResponse cloturer(Long id) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        if (bande.getStatut() != StatutBande.EN_COURS) {
            throw new IllegalArgumentException("La bande doit etre en cours pour etre cloturee.");
        }
        bande.setStatut(StatutBande.TERMINEE);
        bande.setDateSortieReelle(LocalDate.now());
        return toResponse(bande);
    }

    @Transactional
    public void mettreAJourPerformances(Long id) {
        Bande bande = bandeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bande", id));
        List<Animal> animaux = animalRepository.findByBandeId(id);
        if (animaux == null || animaux.isEmpty()) {
            return;
        }
        BigDecimal totalPoids = animaux.stream()
                .map(Animal::getPoidsActuelKg)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPoids.compareTo(BigDecimal.ZERO) > 0 && bande.getPoidsMoyenEntreeKg() != null && bande.getPoidsMoyenEntreeKg().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal poidsMoyenActuel = totalPoids.divide(BigDecimal.valueOf(animaux.size()), 3, RoundingMode.HALF_UP);
            bande.setPoidsMoyenActuelKg(poidsMoyenActuel);
            long ageJours = ChronoUnit.DAYS.between(bande.getDateEntree(), LocalDate.now());
            if (ageJours > 0) {
                BigDecimal difference = poidsMoyenActuel.subtract(bande.getPoidsMoyenEntreeKg());
                BigDecimal gmq = difference.divide(BigDecimal.valueOf(ageJours), 3, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(1000));
                bande.setGainMoyenQuotidienG(gmq);
            }
            if (bande.getFcrCumule() == null && bande.getPoidsMoyenEntreeKg().compareTo(BigDecimal.ZERO) > 0) {
                bande.setFcrCumule(poidsMoyenActuel.divide(bande.getPoidsMoyenEntreeKg(), 3, RoundingMode.HALF_UP));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<BandeResponse> echeances(Long fermeId, int joursHorizon) {
        LocalDate debut = LocalDate.now();
        LocalDate fin = debut.plusDays(joursHorizon);
        return bandeRepository.findBandesSortieProchaineEntre(debut, fin).stream()
                .filter(b -> b.getStructure().getSite().getFerme().getId().equals(fermeId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BandeResponse> animauxDansBande(Long bandeId) {
        Bande bande = bandeRepository.findById(bandeId).orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        List<Animal> animaux = animalRepository.findByBandeId(bandeId);
        List<BandeResponse> result = new ArrayList<>();
        result.add(toResponse(bande));
        return result;
    }

    private String genererCodeBande(BandeRequest request) {
        String prefixe = switch (request.espece()) {
            case POULET, DINDE, CANARD, PINTADE, PIGEON -> "AV";
            case BOVIN -> "BV";
            case OVIN -> "OV";
            case CAPRIN -> "CA";
            case PORC -> "PO";
            case LAPIN -> "LA";
            case TILAPIA, SILURE, CARPE, CREVETTE, CAPITAINE -> "AQ";
            default -> "XX";
        };
        long seq = bandeRepository.count() + 1;
        return String.format("B-%s-%05d", prefixe, seq);
    }

    private void enregistrerMouvementEntree(Bande bande, int quantite) {
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setBande(bande);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setDateMouvement(bande.getDateEntree());
        mouvement.setQuantite(quantite);
        mouvement.setStructureOrigine(bande.getStructure());
        mouvement.setMotif("Creation de bande");
        mouvementRepository.save(mouvement);
    }

    private void enregistrerMouvementTransfert(Bande bande, Structure destination) {
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setBande(bande);
        mouvement.setTypeMouvement(TypeMouvement.TRANSFERT);
        mouvement.setDateMouvement(LocalDate.now());
        mouvement.setStructureOrigine(bande.getStructure());
        mouvement.setStructureDestination(destination);
        mouvement.setMotif("Transfert de bande");
        mouvementRepository.save(mouvement);
    }

    private BandeResponse toResponse(Bande bande) {
        return bandeMapper.toResponse(bande);
    }
}
