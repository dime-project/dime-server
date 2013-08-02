/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.semantic.model;

import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.DUHO;
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCAL;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NDO;
import ie.deri.smile.vocabulary.NEXIF;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NID3;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NMM;
import ie.deri.smile.vocabulary.NMO;
import ie.deri.smile.vocabulary.NRL;
import ie.deri.smile.vocabulary.NUAO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;
import ie.deri.smile.vocabulary.TMO;

import java.util.HashMap;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a temporary (semiauto-generated) class for
 * 
 * @author Ismael Rivera
 */
public class Class4Type {
	
	private static final Logger logger = LoggerFactory.getLogger(Class4Type.class); 
	
	public static <T extends Resource> Class<T> getClassForType(URI type) {
		try {
			if (classes.containsKey(type)) {
				return (Class<T>) Class.forName(classes.get(type));
			} else {
				logger.debug("No mapping found for type "+type);
			}
		} catch (ClassNotFoundException e) {
			logger.debug("No class found for type "+type);
		}
		return null;
	}
	
	private static Map<URI, String> classes;
	static {
		classes = new HashMap<URI, String>();

		classes.put(NCO.Affiliation, "eu.dime.ps.semantic.model.nco.Affiliation");
		classes.put(NCO.AudioIMAccount, "eu.dime.ps.semantic.model.nco.AudioIMAccount");
		classes.put(NCO.BbsNumber, "eu.dime.ps.semantic.model.nco.BbsNumber");
		classes.put(NCO.BirthDate, "eu.dime.ps.semantic.model.nco.BirthDate");
		classes.put(NCO.CarPhoneNumber, "eu.dime.ps.semantic.model.nco.CarPhoneNumber");
		classes.put(NCO.CellPhoneNumber, "eu.dime.ps.semantic.model.nco.CellPhoneNumber");
		classes.put(NCO.Contact, "eu.dime.ps.semantic.model.nco.Contact");
		classes.put(NCO.ContactGroup, "eu.dime.ps.semantic.model.nco.ContactGroup");
		classes.put(NCO.ContactList, "eu.dime.ps.semantic.model.nco.ContactList");
		classes.put(NCO.ContactListDataObject, "eu.dime.ps.semantic.model.nco.ContactListDataObject");
		classes.put(NCO.ContactMedium, "eu.dime.ps.semantic.model.nco.ContactMedium");
		classes.put(NCO.DomesticDeliveryAddress, "eu.dime.ps.semantic.model.nco.DomesticDeliveryAddress");
		classes.put(NCO.EmailAddress, "eu.dime.ps.semantic.model.nco.EmailAddress");
		classes.put(NCO.FaxNumber, "eu.dime.ps.semantic.model.nco.FaxNumber");
		classes.put(NCO.Gender, "eu.dime.ps.semantic.model.nco.Gender");
		classes.put(NCO.Hobby, "eu.dime.ps.semantic.model.nco.Hobby");
		classes.put(NCO.IMAccount, "eu.dime.ps.semantic.model.nco.IMAccount");
		classes.put(NCO.IMCapability, "eu.dime.ps.semantic.model.nco.IMCapability");
		classes.put(NCO.IMStatusType, "eu.dime.ps.semantic.model.nco.IMStatusType");
		classes.put(NCO.InternationalDeliveryAddress, "eu.dime.ps.semantic.model.nco.InternationalDeliveryAddress");
		classes.put(NCO.IsdnNumber, "eu.dime.ps.semantic.model.nco.IsdnNumber");
//		classes.put(NCO.Location, "eu.dime.ps.semantic.model.nco.Location");
		classes.put(NCO.MessagingNumber, "eu.dime.ps.semantic.model.nco.MessagingNumber");
		classes.put(NCO.ModemNumber, "eu.dime.ps.semantic.model.nco.ModemNumber");
		classes.put(NCO.Name, "eu.dime.ps.semantic.model.nco.Name");
		classes.put(NCO.OrganizationContact, "eu.dime.ps.semantic.model.nco.OrganizationContact");
		classes.put(NCO.PagerNumber, "eu.dime.ps.semantic.model.nco.PagerNumber");
		classes.put(NCO.ParcelDeliveryAddress, "eu.dime.ps.semantic.model.nco.ParcelDeliveryAddress");
		classes.put(NCO.PcsNumber, "eu.dime.ps.semantic.model.nco.PcsNumber");
		classes.put(NCO.PersonContact, "eu.dime.ps.semantic.model.nco.PersonContact");
		classes.put(NCO.PersonName, "eu.dime.ps.semantic.model.nco.PersonName");
		classes.put(NCO.PhoneNumber, "eu.dime.ps.semantic.model.nco.PhoneNumber");
		classes.put(NCO.PostalAddress, "eu.dime.ps.semantic.model.nco.PostalAddress");
		classes.put(NCO.Role, "eu.dime.ps.semantic.model.nco.Role");
		classes.put(NCO.VideoIMAccount, "eu.dime.ps.semantic.model.nco.VideoIMAccount");
		classes.put(NCO.VideoTelephoneNumber, "eu.dime.ps.semantic.model.nco.VideoTelephoneNumber");
		classes.put(NCO.VoicePhoneNumber, "eu.dime.ps.semantic.model.nco.VoicePhoneNumber");
		classes.put(TMO.AbilityCarrier, "eu.dime.ps.semantic.model.tmo.AbilityCarrier");
		classes.put(TMO.AbilityCarrierInvolvement, "eu.dime.ps.semantic.model.tmo.AbilityCarrierInvolvement");
		classes.put(TMO.AbilityCarrierRole, "eu.dime.ps.semantic.model.tmo.AbilityCarrierRole");
		classes.put(TMO.AgentAbilityCarrier, "eu.dime.ps.semantic.model.tmo.AgentAbilityCarrier");
		classes.put(TMO.AssociationDependency, "eu.dime.ps.semantic.model.tmo.AssociationDependency");
		classes.put(TMO.Attachment, "eu.dime.ps.semantic.model.tmo.Attachment");
		classes.put(TMO.AttachmentRole, "eu.dime.ps.semantic.model.tmo.AttachmentRole");
		classes.put(TMO.Delegability, "eu.dime.ps.semantic.model.tmo.Delegability");
		classes.put(TMO.Importance, "eu.dime.ps.semantic.model.tmo.Importance");
		classes.put(TMO.Interdependence, "eu.dime.ps.semantic.model.tmo.Interdependence");
		classes.put(TMO.PersonInvolvement, "eu.dime.ps.semantic.model.tmo.PersonInvolvement");
		classes.put(TMO.PersonInvolvementRole, "eu.dime.ps.semantic.model.tmo.PersonInvolvementRole");
		classes.put(TMO.PredecessorDependency, "eu.dime.ps.semantic.model.tmo.PredecessorDependency");
		classes.put(TMO.PredecessorSuccessorDependency, "eu.dime.ps.semantic.model.tmo.PredecessorSuccessorDependency");
		classes.put(TMO.Priority, "eu.dime.ps.semantic.model.tmo.Priority");
		classes.put(TMO.Role, "eu.dime.ps.semantic.model.tmo.Role");
		classes.put(TMO.SimilarityDependence, "eu.dime.ps.semantic.model.tmo.SimilarityDependence");
		classes.put(TMO.Skill, "eu.dime.ps.semantic.model.tmo.Skill");
		classes.put(TMO.StateTypeRole, "eu.dime.ps.semantic.model.tmo.StateTypeRole");
		classes.put(TMO.SuccessorDependency, "eu.dime.ps.semantic.model.tmo.SuccessorDependency");
		classes.put(TMO.SuperSubTaskDependency, "eu.dime.ps.semantic.model.tmo.SuperSubTaskDependency");
		classes.put(TMO.Task, "eu.dime.ps.semantic.model.tmo.Task");
		classes.put(TMO.TaskContainer, "eu.dime.ps.semantic.model.tmo.TaskContainer");
		classes.put(TMO.TaskDependency, "eu.dime.ps.semantic.model.tmo.TaskDependency");
		classes.put(TMO.TaskPrivacyState, "eu.dime.ps.semantic.model.tmo.TaskPrivacyState");
		classes.put(TMO.TaskState, "eu.dime.ps.semantic.model.tmo.TaskState");
		classes.put(TMO.TaskTransmission, "eu.dime.ps.semantic.model.tmo.TaskTransmission");
		classes.put(TMO.TransmissionState, "eu.dime.ps.semantic.model.tmo.TransmissionState");
		classes.put(TMO.TransmissionType, "eu.dime.ps.semantic.model.tmo.TransmissionType");
		classes.put(TMO.UndirectedDependency, "eu.dime.ps.semantic.model.tmo.UndirectedDependency");
		classes.put(TMO.Urgency, "eu.dime.ps.semantic.model.tmo.Urgency");
		classes.put(NDO.DownloadEvent, "eu.dime.ps.semantic.model.ndo.DownloadEvent");
		classes.put(NDO.P2PFile, "eu.dime.ps.semantic.model.ndo.P2PFile");
		classes.put(NDO.Torrent, "eu.dime.ps.semantic.model.ndo.Torrent");
		classes.put(NDO.TorrentedFile, "eu.dime.ps.semantic.model.ndo.TorrentedFile");
		classes.put(NFO.Application, "eu.dime.ps.semantic.model.nfo.Application");
		classes.put(NFO.Archive, "eu.dime.ps.semantic.model.nfo.Archive");
		classes.put(NFO.ArchiveItem, "eu.dime.ps.semantic.model.nfo.ArchiveItem");
		classes.put(NFO.Attachment, "eu.dime.ps.semantic.model.nfo.Attachment");
		classes.put(NFO.Audio, "eu.dime.ps.semantic.model.nfo.Audio");
		classes.put(NFO.Bookmark, "eu.dime.ps.semantic.model.nfo.Bookmark");
		classes.put(NFO.BookmarkFolder, "eu.dime.ps.semantic.model.nfo.BookmarkFolder");
		classes.put(NFO.CompressionType, "eu.dime.ps.semantic.model.nfo.CompressionType");
		classes.put(NFO.Cursor, "eu.dime.ps.semantic.model.nfo.Cursor");
		classes.put(NFO.DataContainer, "eu.dime.ps.semantic.model.nfo.DataContainer");
		classes.put(NFO.DeletedResource, "eu.dime.ps.semantic.model.nfo.DeletedResource");
		classes.put(NFO.Document, "eu.dime.ps.semantic.model.nfo.Document");
		classes.put(NFO.EmbeddedFileDataObject, "eu.dime.ps.semantic.model.nfo.EmbeddedFileDataObject");
		classes.put(NFO.EncryptionStatus, "eu.dime.ps.semantic.model.nfo.EncryptionStatus");
		classes.put(NFO.Executable, "eu.dime.ps.semantic.model.nfo.Executable");
		classes.put(NFO.FileDataObject, "eu.dime.ps.semantic.model.nfo.FileDataObject");
		classes.put(NFO.FileHash, "eu.dime.ps.semantic.model.nfo.FileHash");
		classes.put(NFO.Filesystem, "eu.dime.ps.semantic.model.nfo.Filesystem");
		classes.put(NFO.FilesystemImage, "eu.dime.ps.semantic.model.nfo.FilesystemImage");
		classes.put(NFO.Folder, "eu.dime.ps.semantic.model.nfo.Folder");
		classes.put(NFO.Font, "eu.dime.ps.semantic.model.nfo.Font");
		classes.put(NFO.HardDiskPartition, "eu.dime.ps.semantic.model.nfo.HardDiskPartition");
		classes.put(NFO.HtmlDocument, "eu.dime.ps.semantic.model.nfo.HtmlDocument");
		classes.put(NFO.Icon, "eu.dime.ps.semantic.model.nfo.Icon");
		classes.put(NFO.Image, "eu.dime.ps.semantic.model.nfo.Image");
		classes.put(NFO.Media, "eu.dime.ps.semantic.model.nfo.Media");
		classes.put(NFO.MediaFileListEntry, "eu.dime.ps.semantic.model.nfo.MediaFileListEntry");
		classes.put(NFO.MediaList, "eu.dime.ps.semantic.model.nfo.MediaList");
		classes.put(NFO.MediaStream, "eu.dime.ps.semantic.model.nfo.MediaStream");
		classes.put(NFO.MindMap, "eu.dime.ps.semantic.model.nfo.MindMap");
		classes.put(NFO.OperatingSystem, "eu.dime.ps.semantic.model.nfo.OperatingSystem");
		classes.put(NFO.PaginatedTextDocument, "eu.dime.ps.semantic.model.nfo.PaginatedTextDocument");
		classes.put(NFO.PlainTextDocument, "eu.dime.ps.semantic.model.nfo.PlainTextDocument");
		classes.put(NFO.Presentation, "eu.dime.ps.semantic.model.nfo.Presentation");
		classes.put(NFO.RasterImage, "eu.dime.ps.semantic.model.nfo.RasterImage");
		classes.put(NFO.RemoteDataObject, "eu.dime.ps.semantic.model.nfo.RemoteDataObject");
		classes.put(NFO.RemotePortAddress, "eu.dime.ps.semantic.model.nfo.RemotePortAddress");
		classes.put(NFO.Software, "eu.dime.ps.semantic.model.nfo.Software");
		classes.put(NFO.SoftwareItem, "eu.dime.ps.semantic.model.nfo.SoftwareItem");
		classes.put(NFO.SoftwareService, "eu.dime.ps.semantic.model.nfo.SoftwareService");
		classes.put(NFO.SourceCode, "eu.dime.ps.semantic.model.nfo.SourceCode");
		classes.put(NFO.Spreadsheet, "eu.dime.ps.semantic.model.nfo.Spreadsheet");
		classes.put(NFO.TextDocument, "eu.dime.ps.semantic.model.nfo.TextDocument");
		classes.put(NFO.Trash, "eu.dime.ps.semantic.model.nfo.Trash");
		classes.put(NFO.VectorImage, "eu.dime.ps.semantic.model.nfo.VectorImage");
		classes.put(NFO.Video, "eu.dime.ps.semantic.model.nfo.Video");
		classes.put(NFO.Visual, "eu.dime.ps.semantic.model.nfo.Visual");
		classes.put(NFO.WebDataObject, "eu.dime.ps.semantic.model.nfo.WebDataObject");
		classes.put(NFO.Website, "eu.dime.ps.semantic.model.nfo.Website");
		classes.put(NUAO.DesktopEvent, "eu.dime.ps.semantic.model.nuao.DesktopEvent");
		classes.put(NUAO.Event, "eu.dime.ps.semantic.model.nuao.Event");
		classes.put(NUAO.FocusEvent, "eu.dime.ps.semantic.model.nuao.FocusEvent");
		classes.put(NUAO.ModificationEvent, "eu.dime.ps.semantic.model.nuao.ModificationEvent");
		classes.put(NUAO.UsageEvent, "eu.dime.ps.semantic.model.nuao.UsageEvent");
		classes.put(NMM.Movie, "eu.dime.ps.semantic.model.nmm.Movie");
		classes.put(NMM.MusicAlbum, "eu.dime.ps.semantic.model.nmm.MusicAlbum");
		classes.put(NMM.MusicPiece, "eu.dime.ps.semantic.model.nmm.MusicPiece");
		classes.put(NMM.TVSeries, "eu.dime.ps.semantic.model.nmm.TVSeries");
		classes.put(NMM.TVShow, "eu.dime.ps.semantic.model.nmm.TVShow");
		classes.put(PIMO.Agent, "eu.dime.ps.semantic.model.pimo.Agent");
		classes.put(PIMO.Association, "eu.dime.ps.semantic.model.pimo.Association");
		classes.put(PIMO.Attendee, "eu.dime.ps.semantic.model.pimo.Attendee");
//		classes.put(PIMO.Availability, "eu.dime.ps.semantic.model.pimo.Availability");
		classes.put(PIMO.BlogPost, "eu.dime.ps.semantic.model.pimo.BlogPost");
		classes.put(PIMO.Building, "eu.dime.ps.semantic.model.pimo.Building");
		classes.put(PIMO.City, "eu.dime.ps.semantic.model.pimo.City");
		classes.put(PIMO.ClassOrThing, "eu.dime.ps.semantic.model.pimo.ClassOrThing");
		classes.put(PIMO.ClassOrThingOrPropertyOrAssociation, "eu.dime.ps.semantic.model.pimo.ClassOrThingOrPropertyOrAssociation");
		classes.put(PIMO.ClassRole, "eu.dime.ps.semantic.model.pimo.ClassRole");
		classes.put(PIMO.Collection, "eu.dime.ps.semantic.model.pimo.Collection");
		classes.put(PIMO.Contract, "eu.dime.ps.semantic.model.pimo.Contract");
		classes.put(PIMO.Country, "eu.dime.ps.semantic.model.pimo.Country");
		classes.put(PIMO.Document, "eu.dime.ps.semantic.model.pimo.Document");
		classes.put(PIMO.Event, "eu.dime.ps.semantic.model.pimo.Event");
		classes.put(PIMO.Locatable, "eu.dime.ps.semantic.model.pimo.Locatable");
		classes.put(PIMO.Location, "eu.dime.ps.semantic.model.pimo.Location");
		classes.put(PIMO.LogicalMediaType, "eu.dime.ps.semantic.model.pimo.LogicalMediaType");
		classes.put(PIMO.Meeting, "eu.dime.ps.semantic.model.pimo.Meeting");
		classes.put(PIMO.Note, "eu.dime.ps.semantic.model.pimo.Note");
		classes.put(PIMO.Organization, "eu.dime.ps.semantic.model.pimo.Organization");
		classes.put(PIMO.OrganizationMember, "eu.dime.ps.semantic.model.pimo.OrganizationMember");
		classes.put(PIMO.Person, "eu.dime.ps.semantic.model.pimo.Person");
		classes.put(PIMO.PersonGroup, "eu.dime.ps.semantic.model.pimo.PersonGroup");
		classes.put(PIMO.PersonRole, "eu.dime.ps.semantic.model.pimo.PersonRole");
		classes.put(PIMO.PersonalInformationModel, "eu.dime.ps.semantic.model.pimo.PersonalInformationModel");
//		classes.put(PIMO.Place, "eu.dime.ps.semantic.model.pimo.Place");
		classes.put(PIMO.ProcessConcept, "eu.dime.ps.semantic.model.pimo.ProcessConcept");
		classes.put(PIMO.Project, "eu.dime.ps.semantic.model.pimo.Project");
		classes.put(PIMO.Room, "eu.dime.ps.semantic.model.pimo.Room");
		classes.put(PIMO.SocialEvent, "eu.dime.ps.semantic.model.pimo.SocialEvent");
		classes.put(PIMO.State, "eu.dime.ps.semantic.model.pimo.State");
//		classes.put(PIMO.StatusMessage, "eu.dime.ps.semantic.model.pimo.StatusMessage");
		classes.put(PIMO.Tag, "eu.dime.ps.semantic.model.pimo.Tag");
		classes.put(PIMO.Task, "eu.dime.ps.semantic.model.pimo.Task");
		classes.put(PIMO.Thing, "eu.dime.ps.semantic.model.pimo.Thing");
		classes.put(PIMO.Topic, "eu.dime.ps.semantic.model.pimo.Topic");
//		classes.put(PIMO.User, "eu.dime.ps.semantic.model.pimo.User");
		classes.put(NID3.ID3Audio, "eu.dime.ps.semantic.model.nid3.ID3Audio");
		classes.put(NID3.InvolvedPerson, "eu.dime.ps.semantic.model.nid3.InvolvedPerson");
		classes.put(NID3.SynchronizedText, "eu.dime.ps.semantic.model.nid3.SynchronizedText");
		classes.put(NID3.SynchronizedTextElement, "eu.dime.ps.semantic.model.nid3.SynchronizedTextElement");
		classes.put(NID3.UserDefinedFrame, "eu.dime.ps.semantic.model.nid3.UserDefinedFrame");
		classes.put(NID3.UserDefinedURLFrame, "eu.dime.ps.semantic.model.nid3.UserDefinedURLFrame");
//		classes.put(DCON.Activity, "eu.dime.ps.semantic.model.dcon.Activity");
		classes.put(DCON.Aspect, "eu.dime.ps.semantic.model.dcon.Aspect");
		classes.put(DCON.Attention, "eu.dime.ps.semantic.model.dcon.Attention");
//		classes.put(DCON.Attribute, "eu.dime.ps.semantic.model.dcon.Attribute");
		classes.put(DCON.Connectivity, "eu.dime.ps.semantic.model.dcon.Connectivity");
		classes.put(DCON.Context, "eu.dime.ps.semantic.model.dcon.Context");
		classes.put(DCON.Environment, "eu.dime.ps.semantic.model.dcon.Environment");
		classes.put(DCON.LiveContext, "eu.dime.ps.semantic.model.dcon.LiveContext");
		classes.put(DCON.Peers, "eu.dime.ps.semantic.model.dcon.Peers");
		classes.put(DCON.Schedule, "eu.dime.ps.semantic.model.dcon.Schedule");
		classes.put(DCON.Situation, "eu.dime.ps.semantic.model.dcon.Situation");
		classes.put(DCON.SpaTem, "eu.dime.ps.semantic.model.dcon.SpaTem");
		classes.put(DCON.State, "eu.dime.ps.semantic.model.dcon.State");
		classes.put(DLPO.ActivityPost, "eu.dime.ps.semantic.model.dlpo.ActivityPost");
		classes.put(DLPO.AudioPost, "eu.dime.ps.semantic.model.dlpo.AudioPost");
		classes.put(DLPO.AvailabilityPost, "eu.dime.ps.semantic.model.dlpo.AvailabilityPost");
		classes.put(DLPO.BlogPost, "eu.dime.ps.semantic.model.dlpo.BlogPost");
		classes.put(DLPO.Checkin, "eu.dime.ps.semantic.model.dlpo.Checkin");
		classes.put(DLPO.Comment, "eu.dime.ps.semantic.model.dlpo.Comment");
		classes.put(DLPO.EventPost, "eu.dime.ps.semantic.model.dlpo.EventPost");
		classes.put(DLPO.ImagePost, "eu.dime.ps.semantic.model.dlpo.ImagePost");
		classes.put(DLPO.LivePost, "eu.dime.ps.semantic.model.dlpo.LivePost");
		classes.put(DLPO.MultimediaPost, "eu.dime.ps.semantic.model.dlpo.MultimediaPost");
		classes.put(DLPO.NotePost, "eu.dime.ps.semantic.model.dlpo.NotePost");
		classes.put(DLPO.PresencePost, "eu.dime.ps.semantic.model.dlpo.PresencePost");
		classes.put(DLPO.Status, "eu.dime.ps.semantic.model.dlpo.Status");
		classes.put(DLPO.VideoPost, "eu.dime.ps.semantic.model.dlpo.VideoPost");
		classes.put(DLPO.WebDocumentPost, "eu.dime.ps.semantic.model.dlpo.WebDocumentPost");
		classes.put(DUHO.Log, "eu.dime.ps.semantic.model.duho.Log");
		classes.put(DUHO.ContextLog, "eu.dime.ps.semantic.model.duho.ContextLog");
		classes.put(DUHO.PrivacyPreferenceLog, "eu.dime.ps.semantic.model.duho.PrivacyPreferenceLog");
		classes.put(DDO.Bluetooth, "eu.dime.ps.semantic.model.ddo.Bluetooth");
		classes.put(DDO.CellularNetwork, "eu.dime.ps.semantic.model.ddo.CellularNetwork");
		classes.put(DDO.Network, "eu.dime.ps.semantic.model.ddo.Network");
		classes.put(DDO.Device, "eu.dime.ps.semantic.model.ddo.Device");
		classes.put(DDO.Ethernet, "eu.dime.ps.semantic.model.ddo.Ethernet");
		classes.put(DDO.WiFi, "eu.dime.ps.semantic.model.ddo.WiFi");
		classes.put(PPO.AccessSpace, "eu.dime.ps.semantic.model.ppo.AccessSpace");
		classes.put(PPO.Condition, "eu.dime.ps.semantic.model.ppo.Condition");
		classes.put(PPO.PrivacyPreference, "eu.dime.ps.semantic.model.ppo.PrivacyPreference");
		classes.put(NRL.AsymmetricProperty, "eu.dime.ps.semantic.model.nrl.AsymmetricProperty");
		classes.put(NRL.Configuration, "eu.dime.ps.semantic.model.nrl.Configuration");
		classes.put(NRL.Data, "eu.dime.ps.semantic.model.nrl.Data");
		classes.put(NRL.DiscardableInstanceBase, "eu.dime.ps.semantic.model.nrl.DiscardableInstanceBase");
		classes.put(NRL.DocumentGraph, "eu.dime.ps.semantic.model.nrl.DocumentGraph");
		classes.put(NRL.ExternalViewSpecification, "eu.dime.ps.semantic.model.nrl.ExternalViewSpecification");
		classes.put(NRL.FunctionalProperty, "eu.dime.ps.semantic.model.nrl.FunctionalProperty");
		classes.put(NRL.Graph, "eu.dime.ps.semantic.model.nrl.Graph");
		classes.put(NRL.GraphMetadata, "eu.dime.ps.semantic.model.nrl.GraphMetadata");
		classes.put(NRL.GraphView, "eu.dime.ps.semantic.model.nrl.GraphView");
		classes.put(NRL.InstanceBase, "eu.dime.ps.semantic.model.nrl.InstanceBase");
		classes.put(NRL.InverseFunctionalProperty, "eu.dime.ps.semantic.model.nrl.InverseFunctionalProperty");
		classes.put(NRL.KnowledgeBase, "eu.dime.ps.semantic.model.nrl.KnowledgeBase");
		classes.put(NRL.Ontology, "eu.dime.ps.semantic.model.nrl.Ontology");
		classes.put(NRL.ReflexiveProperty, "eu.dime.ps.semantic.model.nrl.ReflexiveProperty");
		classes.put(NRL.RuleViewSpecification, "eu.dime.ps.semantic.model.nrl.RuleViewSpecification");
		classes.put(NRL.Schema, "eu.dime.ps.semantic.model.nrl.Schema");
		classes.put(NRL.Semantics, "eu.dime.ps.semantic.model.nrl.Semantics");
		classes.put(NRL.SymmetricProperty, "eu.dime.ps.semantic.model.nrl.SymmetricProperty");
		classes.put(NRL.TransitiveProperty, "eu.dime.ps.semantic.model.nrl.TransitiveProperty");
		classes.put(NRL.ViewSpecification, "eu.dime.ps.semantic.model.nrl.ViewSpecification");
		classes.put(NEXIF.Photo, "eu.dime.ps.semantic.model.nexif.Photo");
		classes.put(NMO.Email, "eu.dime.ps.semantic.model.nmo.Email");
		classes.put(NMO.IMMessage, "eu.dime.ps.semantic.model.nmo.IMMessage");
		classes.put(NMO.Mailbox, "eu.dime.ps.semantic.model.nmo.Mailbox");
		classes.put(NMO.MailboxDataObject, "eu.dime.ps.semantic.model.nmo.MailboxDataObject");
		classes.put(NMO.Message, "eu.dime.ps.semantic.model.nmo.Message");
		classes.put(NMO.MessageHeader, "eu.dime.ps.semantic.model.nmo.MessageHeader");
		classes.put(NMO.MimeEntity, "eu.dime.ps.semantic.model.nmo.MimeEntity");
		classes.put(NAO.Agent, "eu.dime.ps.semantic.model.nao.Agent");
		classes.put(NAO.FreeDesktopIcon, "eu.dime.ps.semantic.model.nao.FreeDesktopIcon");
		classes.put(NAO.Party, "eu.dime.ps.semantic.model.nao.Party");
		classes.put(NAO.Symbol, "eu.dime.ps.semantic.model.nao.Symbol");
		classes.put(NAO.Tag, "eu.dime.ps.semantic.model.nao.Tag");
		classes.put(DAO.Account, "eu.dime.ps.semantic.model.dao.Account");
		classes.put(DAO.Credentials, "eu.dime.ps.semantic.model.dao.Credentials");
		classes.put(NIE.DataObject, "eu.dime.ps.semantic.model.nie.DataObject");
		classes.put(NIE.DataSource, "eu.dime.ps.semantic.model.nie.DataSource");
		classes.put(NIE.InformationElement, "eu.dime.ps.semantic.model.nie.InformationElement");
		classes.put(new URIImpl("http://www.w3.org/2004/03/trix/rdfg-1/Graph"), "eu.dime.ps.semantic.model.rdfg1.Graph");
		classes.put(NCAL.AccessClassification, "eu.dime.ps.semantic.model.ncal.AccessClassification");
		classes.put(NCAL.Alarm, "eu.dime.ps.semantic.model.ncal.Alarm");
		classes.put(NCAL.AlarmAction, "eu.dime.ps.semantic.model.ncal.AlarmAction");
		classes.put(NCAL.Attachment, "eu.dime.ps.semantic.model.ncal.Attachment");
		classes.put(NCAL.AttachmentEncoding, "eu.dime.ps.semantic.model.ncal.AttachmentEncoding");
		classes.put(NCAL.Attendee, "eu.dime.ps.semantic.model.ncal.Attendee");
		classes.put(NCAL.AttendeeOrOrganizer, "eu.dime.ps.semantic.model.ncal.AttendeeOrOrganizer");
		classes.put(NCAL.AttendeeRole, "eu.dime.ps.semantic.model.ncal.AttendeeRole");
		classes.put(NCAL.BydayRulePart, "eu.dime.ps.semantic.model.ncal.BydayRulePart");
		classes.put(NCAL.Calendar, "eu.dime.ps.semantic.model.ncal.Calendar");
		classes.put(NCAL.CalendarDataObject, "eu.dime.ps.semantic.model.ncal.CalendarDataObject");
		classes.put(NCAL.CalendarScale, "eu.dime.ps.semantic.model.ncal.CalendarScale");
		classes.put(NCAL.CalendarUserType, "eu.dime.ps.semantic.model.ncal.CalendarUserType");
		classes.put(NCAL.Event, "eu.dime.ps.semantic.model.ncal.Event");
		classes.put(NCAL.EventStatus, "eu.dime.ps.semantic.model.ncal.EventStatus");
		classes.put(NCAL.Freebusy, "eu.dime.ps.semantic.model.ncal.Freebusy");
		classes.put(NCAL.FreebusyPeriod, "eu.dime.ps.semantic.model.ncal.FreebusyPeriod");
		classes.put(NCAL.FreebusyType, "eu.dime.ps.semantic.model.ncal.FreebusyType");
		classes.put(NCAL.Journal, "eu.dime.ps.semantic.model.ncal.Journal");
		classes.put(NCAL.JournalStatus, "eu.dime.ps.semantic.model.ncal.JournalStatus");
		classes.put(NCAL.NcalDateTime, "eu.dime.ps.semantic.model.ncal.NcalDateTime");
		classes.put(NCAL.NcalPeriod, "eu.dime.ps.semantic.model.ncal.NcalPeriod");
		classes.put(NCAL.NcalTimeEntity, "eu.dime.ps.semantic.model.ncal.NcalTimeEntity");
		classes.put(NCAL.Organizer, "eu.dime.ps.semantic.model.ncal.Organizer");
		classes.put(NCAL.ParticipationStatus, "eu.dime.ps.semantic.model.ncal.ParticipationStatus");
		classes.put(NCAL.RecurrenceFrequency, "eu.dime.ps.semantic.model.ncal.RecurrenceFrequency");
		classes.put(NCAL.RecurrenceIdentifier, "eu.dime.ps.semantic.model.ncal.RecurrenceIdentifier");
		classes.put(NCAL.RecurrenceIdentifierRange, "eu.dime.ps.semantic.model.ncal.RecurrenceIdentifierRange");
		classes.put(NCAL.RecurrenceRule, "eu.dime.ps.semantic.model.ncal.RecurrenceRule");
		classes.put(NCAL.RequestStatus, "eu.dime.ps.semantic.model.ncal.RequestStatus");
		classes.put(NCAL.TimeTransparency, "eu.dime.ps.semantic.model.ncal.TimeTransparency");
		classes.put(NCAL.Timezone, "eu.dime.ps.semantic.model.ncal.Timezone");
		classes.put(NCAL.TimezoneObservance, "eu.dime.ps.semantic.model.ncal.TimezoneObservance");
		classes.put(NCAL.Todo, "eu.dime.ps.semantic.model.ncal.Todo");
		classes.put(NCAL.TodoStatus, "eu.dime.ps.semantic.model.ncal.TodoStatus");
		classes.put(NCAL.Trigger, "eu.dime.ps.semantic.model.ncal.Trigger");
		classes.put(NCAL.TriggerRelation, "eu.dime.ps.semantic.model.ncal.TriggerRelation");
		classes.put(NCAL.UnionOfAlarmEventFreebusyJournalTodo, "eu.dime.ps.semantic.model.ncal.UnionOfAlarmEventFreebusyJournalTodo");
		classes.put(NCAL.UnionOfAlarmEventFreebusyTodo, "eu.dime.ps.semantic.model.ncal.UnionOfAlarmEventFreebusyTodo");
		classes.put(NCAL.UnionOfAlarmEventJournalTodo, "eu.dime.ps.semantic.model.ncal.UnionOfAlarmEventJournalTodo");
		classes.put(NCAL.UnionOfAlarmEventTodo, "eu.dime.ps.semantic.model.ncal.UnionOfAlarmEventTodo");
		classes.put(NCAL.UnionOfEventFreebusy, "eu.dime.ps.semantic.model.ncal.UnionOfEventFreebusy");
		classes.put(NCAL.UnionOfEventFreebusyJournalTodo, "eu.dime.ps.semantic.model.ncal.UnionOfEventFreebusyJournalTodo");
		classes.put(NCAL.UnionOfEventJournalTimezoneTodo, "eu.dime.ps.semantic.model.ncal.UnionOfEventJournalTimezoneTodo");
		classes.put(NCAL.UnionOfEventJournalTodo, "eu.dime.ps.semantic.model.ncal.UnionOfEventJournalTodo");
		classes.put(NCAL.UnionOfEventTodo, "eu.dime.ps.semantic.model.ncal.UnionOfEventTodo");
		classes.put(NCAL.UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo, "eu.dime.ps.semantic.model.ncal.UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo");
		classes.put(NCAL.UnionOfTimezoneObservanceEventFreebusyTimezoneTodo, "eu.dime.ps.semantic.model.ncal.UnionOfTimezoneObservanceEventFreebusyTimezoneTodo");
		classes.put(NCAL.UnionOfTimezoneObservanceEventJournalTimezoneTodo, "eu.dime.ps.semantic.model.ncal.UnionOfTimezoneObservanceEventJournalTimezoneTodo");
		classes.put(NCAL.UnionParentClass, "eu.dime.ps.semantic.model.ncal.UnionParentClass");
		classes.put(NCAL.Weekday, "eu.dime.ps.semantic.model.ncal.Weekday");
		classes.put(GEO.Point, "eu.dime.ps.semantic.model.geo.Point");
		classes.put(GEO.SpatialThing, "eu.dime.ps.semantic.model.geo.SpatialThing");
	}
}
