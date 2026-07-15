package com.reseau_partage.organisation.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Batiment;
import com.reseau_partage.core.entities.Enclos;
import com.reseau_partage.core.entities.Entrepot;
import com.reseau_partage.core.entities.Etang;
import com.reseau_partage.core.entities.Ferme;
import com.reseau_partage.core.entities.Parcelle;
import com.reseau_partage.core.entities.Porcherie;
import com.reseau_partage.core.entities.Poulailler;
import com.reseau_partage.core.entities.Site;
import com.reseau_partage.core.entities.StatutFerme;
import com.reseau_partage.core.entities.StatutSite;
import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.repository.FermeRepository;
import com.reseau_partage.core.repository.SiteRepository;
import com.reseau_partage.core.repository.StructureRepository;
import com.reseau_partage.organisation.dto.FermeRequest;
import com.reseau_partage.organisation.dto.SiteRequest;
import com.reseau_partage.organisation.dto.StructureRequest;
import com.reseau_partage.organisation.exception.ConflictException;
import com.reseau_partage.organisation.exception.ResourceNotFoundException;
import com.reseau_partage.organisation.exception.StatutTransitionException;

@Service
@Transactional
public class OrganisationService {
  private final FermeRepository fermes;
  private final SiteRepository sites;
  private final StructureRepository structures;

  public OrganisationService(FermeRepository fermes, SiteRepository sites, StructureRepository structures) {
    this.fermes = fermes;
    this.sites = sites;
    this.structures = structures;
  }

  public Map<String, Object> createFerme(FermeRequest r) {
    if (fermes.existsByNomAndPays(r.nom(), r.pays()))
      throw new ConflictException("Une ferme portant ce nom existe deja dans ce pays.");
    Ferme f = new Ferme();
    apply(f, r);
    f.setStatut(StatutFerme.ACTIVE);
    return ferme(fermes.save(f));
  }

  @Transactional(readOnly = true)
  public List<Map<String, Object>> listFermes() {
    return fermes.findByStatut(StatutFerme.ACTIVE).stream().map(this::ferme).toList();
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getFerme(Long id) {
    return ferme(getFermeEntity(id));
  }

  public Map<String, Object> updateFerme(Long id, FermeRequest r) {
    Ferme f = getFermeEntity(id);
    apply(f, r);
    return ferme(f);
  }

  public void archiveFerme(Long id) {
    Ferme f = getFermeEntity(id);
    f.setStatut(StatutFerme.ARCHIVEE);
    for (Site s : sites.findByFermeId(id)) {
      s.setStatut(StatutSite.ARCHIVE);
      structures.findBySiteId(s.getId()).forEach(x -> x.setStatut(StatutStructure.ARCHIVE));
    }
  }

  @Transactional(readOnly = true)
  public Map<String, Object> fermeStats(Long id) {
    Ferme f = getFermeEntity(id);
    List<Site> ss = sites.findByFermeId(id);
    long count = ss.stream().mapToLong(s -> structures.countBySiteId(s.getId())).sum();
    return Map.of("fermeId", f.getId(), "nombreSites", ss.size(), "nombreStructures", count);
  }

  public Map<String, Object> createSite(SiteRequest r) {
    Ferme f = getFermeEntity(r.fermeId());
    if (f.getStatut() != StatutFerme.ACTIVE)
      throw new IllegalArgumentException("La ferme doit etre active.");
    if (sites.existsByNomAndFermeId(r.nom(), f.getId()))
      throw new ConflictException("Un site portant ce nom existe deja dans cette ferme.");
    Site s = new Site();
    s.setFerme(f);
    apply(s, r);
    s.setStatut(StatutSite.ACTIF);
    return site(sites.save(s));
  }

  @Transactional(readOnly = true)
  public List<Map<String, Object>> listSites(Long fermeId) {
    getFermeEntity(fermeId);
    return sites.findByFermeId(fermeId).stream().map(this::site).toList();
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getSite(Long id) {
    return site(getSiteEntity(id));
  }

  public Map<String, Object> updateSite(Long id, SiteRequest r) {
    Site s = getSiteEntity(id);
    Ferme f = getFermeEntity(r.fermeId());
    if (!s.getFerme().getId().equals(f.getId()) && sites.existsByNomAndFermeId(r.nom(), f.getId()))
      throw new ConflictException("Un site portant ce nom existe deja dans cette ferme.");
    s.setFerme(f);
    apply(s, r);
    return site(s);
  }

  public Map<String, Object> siteStatus(Long id, StatutSite status) {
    Site s = getSiteEntity(id);
    s.setStatut(status);
    if (status == StatutSite.ARCHIVE)
      structures.findBySiteId(id).forEach(x -> x.setStatut(StatutStructure.ARCHIVE));
    return site(s);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> siteStats(Long id) {
    Site s = getSiteEntity(id);
    Map<String, Long> types = new TreeMap<>();
    for (Structure x : structures.findBySiteId(id))
      types.merge(type(x), 1L, Long::sum);
    return Map.of("siteId", s.getId(), "nombreStructures", types.values().stream().mapToLong(Long::longValue).sum(),
        "structuresParType", types, "nombreAnimaux", 0, "nombreEmployes", 0);
  }

  public Map<String, Object> createStructure(StructureRequest r) {
    Site site = getSiteEntity(r.siteId());
    if (site.getStatut() != StatutSite.ACTIF)
      throw new IllegalArgumentException("Le site doit etre actif.");
    Structure s = newStructure(r.typeStructure());
    s.setSite(site);
    apply(s, r);
    s.setStatut(StatutStructure.ACTIF);
    return structure(structures.save(s));
  }

  @Transactional(readOnly = true)
  public List<Map<String, Object>> listStructuresForSite(Long id) {
    getSiteEntity(id);
    return structures.findBySiteId(id).stream().map(this::structure).toList();
  }

  @Transactional(readOnly = true)
  public List<Map<String, Object>> listStructuresForFerme(Long id) {
    getFermeEntity(id);
    return structures.findBySiteFermeId(id).stream().map(this::structure).toList();
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getStructure(Long id) {
    return structure(getStructureEntity(id));
  }

  public Map<String, Object> updateStructure(Long id, StructureRequest r) {
    Structure s = getStructureEntity(id);
    if (!type(s).equals(normalizeType(r.typeStructure())))
      throw new IllegalArgumentException("Le type d'une structure ne peut pas etre modifie.");
    s.setSite(getSiteEntity(r.siteId()));
    apply(s, r);
    return structure(s);
  }

  public Map<String, Object> structureStatus(Long id, StatutStructure next) {
    Structure s = getStructureEntity(id);
    if (!allowed(s.getStatut(), next))
      throw new StatutTransitionException(s.getStatut(), next);
    s.setStatut(next);
    return structure(s);
  }

  public Map<String, Object> startSanitaryVacancy(Long id) {
    Structure s = getStructureEntity(id);
    if (s.getStatut() != StatutStructure.ACTIF)
      throw new StatutTransitionException(s.getStatut(), StatutStructure.VIDE_SANITAIRE);
    s.setStatut(StatutStructure.VIDE_SANITAIRE);
    s.setDateDebutVide(LocalDateTime.now());
    return structure(s);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> occupancy(Long id) {
    Structure s = getStructureEntity(id);
    Integer capacity = s instanceof Batiment b   ? b.getCapaciteMaxAnimaux()
                     : s instanceof Enclos e      ? e.getCapaciteMaxAnimaux()
                     : s instanceof Poulailler p  ? p.getCapaciteMaxAnimaux()
                     : s instanceof Porcherie pc  ? pc.getCapaciteMaxAnimaux()
                     : null;
    Map<String, Object> out = new LinkedHashMap<>();
    out.put("structureId", id);
    out.put("typeStructure", type(s));
    out.put("capaciteMaxAnimaux", capacity);
    out.put("animauxPresents", 0);
    out.put("tauxOccupation", capacity == null ? null : 0);
    out.put("niveauAlerte", null);
    return out;
  }

  private Ferme getFermeEntity(Long id) {
    return fermes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ferme", id));
  }

  private Site getSiteEntity(Long id) {
    return sites.findById(id).orElseThrow(() -> new ResourceNotFoundException("Site", id));
  }

  private Structure getStructureEntity(Long id) {
    return structures.findById(id).orElseThrow(() -> new ResourceNotFoundException("Structure", id));
  }

  private void apply(Ferme f, FermeRequest r) {
    f.setNom(r.nom());
    f.setPays(r.pays());
    f.setDevise(r.devise());
    f.setFuseauHoraire(r.fuseauHoraire());
    f.setSuperficieTotale(r.superficieTotale());
    f.setLogoUrl(r.logoUrl());
    f.setTelephoneContact(r.telephoneContact());
    f.setEmailContact(r.emailContact());
  }

  private void apply(Site s, SiteRequest r) {
    s.setNom(r.nom());
    s.setAdresse(r.adresse());
    s.setVille(r.ville());
    s.setRegion(r.region());
    s.setLatitude(r.latitude());
    s.setLongitude(r.longitude());
    s.setSuperficie(r.superficie());
    s.setResponsableNom(r.responsableNom());
    s.setResponsableTelephone(r.responsableTelephone());
  }

  private void apply(Structure s, StructureRequest r) {
    s.setNom(r.nom());
    s.setDescription(r.description());
    s.setSuperficieM2(r.superficieM2());
    s.setLatitude(r.latitude());
    s.setLongitude(r.longitude());
    if (s instanceof Batiment x) {
      x.setCapaciteMaxAnimaux(r.capaciteMaxAnimaux());
      x.setTypeVentilation(r.typeVentilation());
      x.setDureeVideSanitaireJours(r.dureeVideSanitaireJours());
      x.setNombreRangees(r.nombreRangees());
      x.setSystemeAbreuvement(r.systemeAbreuvement());
    } else if (s instanceof Enclos x) {
      x.setCapaciteMaxAnimaux(r.capaciteMaxAnimaux());
      x.setTypeCloture(r.typeCloture());
      x.setAccesEau(r.accesEau());
      x.setEspecesCompatibles(r.especesCompatibles());
    } else if (s instanceof Etang x) {
      x.setVolumeM3(r.volumeM3());
      x.setProfondeurM(r.profondeurM());
      x.setSystemeAeration(r.systemeAeration());
      x.setTemperatureCibleCelsius(r.temperatureCibleCelsius());
      x.setPhCible(r.phCible());
    } else if (s instanceof Entrepot x) {
      x.setCapaciteTonnes(r.capaciteTonnes());
      x.setTemperatureControlee(r.temperatureControlee());
      x.setTemperatureMinCelsius(r.temperatureMinCelsius());
      x.setTemperatureMaxCelsius(r.temperatureMaxCelsius());
    } else if (s instanceof Parcelle x) {
      x.setTypeSol(r.typeSol());
      x.setCultureActuelle(r.cultureActuelle());
      x.setSystemeIrrigation(r.systemeIrrigation());
      x.setCoordonneesPolygone(r.coordonneesPolygone());
    } else if (s instanceof Poulailler x) {
      x.setCapaciteMaxAnimaux(r.capaciteMaxAnimaux());
      x.setTypeVentilation(r.typeVentilation());
      x.setDureeVideSanitaireJours(r.dureeVideSanitaireJours());
      x.setNombreRangees(r.nombreRangees());
      x.setSystemeAbreuvement(r.systemeAbreuvement());
      x.setTypeProduction(r.typeProduction());
      x.setSystemeChauffage(r.systemeChauffage());
    } else if (s instanceof Porcherie x) {
      x.setCapaciteMaxAnimaux(r.capaciteMaxAnimaux());
      x.setTypeSol(r.typeSol());
      x.setDureeVideSanitaireJours(r.dureeVideSanitaireJours());
      x.setSystemeAbreuvement(r.systemeAbreuvement());
      x.setSystemeEvacuation(r.systemeEvacuation());
      x.setNombreCases(r.nombreCases());
      x.setTypeVentilation(r.typeVentilation());
    }
  }

  private Structure newStructure(String value) {
    return switch (normalizeType(value)) {
      case "BATIMENT"   -> new Batiment();
      case "ENCLOS"     -> new Enclos();
      case "ETANG"      -> new Etang();
      case "ENTREPOT"   -> new Entrepot();
      case "PARCELLE"   -> new Parcelle();
      case "POULAILLER" -> new Poulailler();
      case "PORCHERIE"  -> new Porcherie();
      default -> throw new IllegalArgumentException(
          "Type de structure inconnu : " + value +
          ". Valeurs acceptées : BATIMENT, ENCLOS, ETANG, ENTREPOT, PARCELLE, POULAILLER, PORCHERIE.");
    };
  }

  private String normalizeType(String v) {
    return v.trim().toUpperCase(Locale.ROOT);
  }

  private String type(Structure s) {
    if (s instanceof Batiment)   return "BATIMENT";
    if (s instanceof Enclos)     return "ENCLOS";
    if (s instanceof Etang)      return "ETANG";
    if (s instanceof Entrepot)   return "ENTREPOT";
    if (s instanceof Poulailler) return "POULAILLER";
    if (s instanceof Porcherie)  return "PORCHERIE";
    return "PARCELLE";
  }

  private boolean allowed(StatutStructure f, StatutStructure t) {
    return (f == StatutStructure.ACTIF && (t == StatutStructure.VIDE_SANITAIRE || t == StatutStructure.ARCHIVE))
        || (f == StatutStructure.VIDE_SANITAIRE && t == StatutStructure.EN_DESINFECTION)
        || (f == StatutStructure.EN_DESINFECTION && t == StatutStructure.PRET)
        || (f == StatutStructure.PRET && t == StatutStructure.ACTIF);
  }

  private Map<String, Object> ferme(Ferme f) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", f.getId());
    m.put("nom", f.getNom());
    m.put("pays", f.getPays());
    m.put("devise", f.getDevise());
    m.put("fuseauHoraire", f.getFuseauHoraire());
    m.put("superficieTotale", f.getSuperficieTotale());
    m.put("logoUrl", f.getLogoUrl());
    m.put("telephoneContact", f.getTelephoneContact());
    m.put("emailContact", f.getEmailContact());
    m.put("statut", f.getStatut());
    m.put("dateCreation", f.getDateCreation());
    m.putAll(fermeStats(f.getId()));
    return m;
  }

  private Map<String, Object> site(Site s) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", s.getId());
    m.put("fermeId", s.getFerme().getId());
    m.put("fermeNom", s.getFerme().getNom());
    m.put("nom", s.getNom());
    m.put("adresse", s.getAdresse());
    m.put("ville", s.getVille());
    m.put("region", s.getRegion());
    m.put("latitude", s.getLatitude());
    m.put("longitude", s.getLongitude());
    m.put("superficie", s.getSuperficie());
    m.put("responsableNom", s.getResponsableNom());
    m.put("responsableTelephone", s.getResponsableTelephone());
    m.put("statut", s.getStatut());
    m.put("dateCreation", s.getDateCreation());
    m.put("nombreStructures", structures.countBySiteId(s.getId()));
    return m;
  }

  private Map<String, Object> structure(Structure s) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", s.getId());
    m.put("siteId", s.getSite().getId());
    m.put("siteNom", s.getSite().getNom());
    m.put("nom", s.getNom());
    m.put("typeStructure", type(s));
    m.put("description", s.getDescription());
    m.put("superficieM2", s.getSuperficieM2());
    m.put("latitude", s.getLatitude());
    m.put("longitude", s.getLongitude());
    m.put("statut", s.getStatut());
    m.put("dateCreation", s.getDateCreation());
    m.put("dateDebutVide", s.getDateDebutVide());
    return m;
  }
}
