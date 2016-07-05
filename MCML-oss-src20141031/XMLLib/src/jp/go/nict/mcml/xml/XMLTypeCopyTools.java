package jp.go.nict.mcml.xml;

import org.apache.log4j.Logger;

import com.MCML.AccentType;
import com.MCML.AttachedBinaryType;
import com.MCML.AudioType;
import com.MCML.ChunkType;
import com.MCML.CustomType;
import com.MCML.DataType;
import com.MCML.DetailType;
import com.MCML.DeviceType;
import com.MCML.ErrorType;
import com.MCML.FromType;
import com.MCML.GlobalPositionType;
import com.MCML.HistoryType;
import com.MCML.HypothesisFormatType;
import com.MCML.IPAType;
import com.MCML.ImageType;
import com.MCML.InputModalityType;
import com.MCML.InputType;
import com.MCML.InputUserProfileType;
import com.MCML.ItemType;
import com.MCML.LanguageType;
import com.MCML.LanguageTypeType;
import com.MCML.ListType;
import com.MCML.LocationType;
import com.MCML.MCMLType;
import com.MCML.MapType;
import com.MCML.ModelTypeType;
import com.MCML.OptionType;
import com.MCML.OutputType;
import com.MCML.POSType;
import com.MCML.PersonalityType;
import com.MCML.PronunciationType;
import com.MCML.ReceiverType;
import com.MCML.RequestType;
import com.MCML.ResponseType;
import com.MCML.RoutingType;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.ServerType;
import com.MCML.SignalType;
import com.MCML.SpeakingType;
import com.MCML.SurfaceType;
import com.MCML.SurfaceType2;
import com.MCML.TargetOutputType;
import com.MCML.TextType;
import com.MCML.ToType;
import com.MCML.TransmitterType;
import com.MCML.UserProfileType;
import com.MCML.UserType;
import com.MCML.VideoType;
import com.MCML.WritingType;

/**
 * Common copy MCML elements
 *
 * @author knozaki
 * @version 4.0
 * @since 20120921
 */
public class XMLTypeCopyTools {
	private static final Logger log = Logger.getLogger(XMLTypeCopyTools.class
			.getName());

	/**
	 * Copy down from the User element
	 *
	 * @param distUserType
	 * @param sourUserType
	 */
	public static void copyUserType(UserType distUserType, UserType sourUserType) {
		if (sourUserType.Receiver.exists()) {
			for (int i = 0; i < sourUserType.Receiver.count(); i++) {
				ReceiverType sourReceiverType = sourUserType.Receiver.at(i);
				ReceiverType destReceiverType = distUserType.Receiver.append();

				copyReceiverType(destReceiverType, sourReceiverType);

			}
		}
		if (sourUserType.Transmitter.exists()) {
			for (int i = 0; i < sourUserType.Transmitter.count(); i++) {
				TransmitterType sourTransmitterType = sourUserType.Transmitter.at(i);
				TransmitterType destTransmitterType = distUserType.Transmitter.append();

				copyTransmitterType(destTransmitterType, sourTransmitterType);
			}
		}

	}

	/**
	 * Copy down from the Receiver element "USER - Receiver"
	 *
	 * @param destReceiverType
	 * @param sourReceiverType
	 */
	public static void copyReceiverType(ReceiverType destReceiverType, ReceiverType sourReceiverType) {
		if (sourReceiverType.Device.exists()) {
			for (int i = 0; i < sourReceiverType.Device.count(); i++) {
				DeviceType sourDeviceType = sourReceiverType.Device.at(i);
				DeviceType destDeviceType = destReceiverType.Device.append();

				copyDeviceType(destDeviceType, sourDeviceType);
			}

		}
		if (sourReceiverType.UserProfile.exists()) {
			for (int i = 0; i < sourReceiverType.UserProfile.count(); i++) {
				UserProfileType sourUserProfileType = sourReceiverType.UserProfile.at(i);
				UserProfileType destUserProfileType = destReceiverType.UserProfile.append();

				copyUserProfileType(destUserProfileType, sourUserProfileType);
			}
		}
	}

	/**
	 * Copy down from the Transmitter element "USER - Transmitter"
	 *
	 * @param destTransmitterType
	 * @param sourTransmitterType
	 */
	public static void copyTransmitterType(TransmitterType destTransmitterType, TransmitterType sourTransmitterType) {
		if (sourTransmitterType.Device.exists()) {
			for (int i = 0; i < sourTransmitterType.Device.count(); i++) {
				DeviceType sourDeviceType = sourTransmitterType.Device.at(i);
				DeviceType destDeviceType = destTransmitterType.Device.append();

				copyDeviceType(destDeviceType, sourDeviceType);
			}
		}
		if (sourTransmitterType.UserProfile.exists()) {
			for (int i = 0; i < sourTransmitterType.UserProfile.count(); i++) {
				UserProfileType sourUserProfileType = sourTransmitterType.UserProfile.at(i);
				UserProfileType destUserProfileType = destTransmitterType.UserProfile.append();

				copyUserProfileType(destUserProfileType, sourUserProfileType);
			}
		}
	}

	public static void copyDeviceType(DeviceType destDeviceType, DeviceType sourDeviceType) {
		if (sourDeviceType.Location.exists()) {
			for (int i = 0; i < sourDeviceType.Location.count(); i++) {
				LocationType sourLocationType = sourDeviceType.Location.at(i);
				LocationType destLocationType = destDeviceType.Location.append();

				copyLocationType(destLocationType, sourLocationType);
			}
		}
	}

	/**
	 * @param destLocationType
	 * @param sourLocationType
	 */
	public static void copyLocationType(LocationType destLocationType, LocationType sourLocationType) {
		if (sourLocationType.GlobalPosition.exists()) {
			for (int i = 0; i < sourLocationType.GlobalPosition.count(); i++) {
				GlobalPositionType sourGlobalPositionType = sourLocationType.GlobalPosition.at(i);
				GlobalPositionType destGlobalPositionType = destLocationType.GlobalPosition.append();

				if (sourGlobalPositionType.Latitude.exists()) {
					destGlobalPositionType.Latitude.setValue(sourGlobalPositionType.Latitude.getValue());
				}
				if (sourGlobalPositionType.Longitude.exists()) {
					destGlobalPositionType.Longitude.setValue(sourGlobalPositionType.Longitude.getValue());
				}
			}
		}
		if (sourLocationType.URI.exists()) {
			for (int i = 0; i < sourLocationType.URI.count(); i++) {
				destLocationType.URI.append().setValue(sourLocationType.URI.at(i).getValue());
			}
		}
	}

	/**
	 * Copy down from the UserProfile element "USER - Receiver or Transmitter - UserProfile"
	 *
	 * @param destUserProfileType
	 * @param sourUserProfileType
	 */
	public static void copyUserProfileType(UserProfileType destUserProfileType, UserProfileType sourUserProfileType) {
		if (sourUserProfileType.Age.exists()) {
			destUserProfileType.Age.setValue(sourUserProfileType.Age.getValue());
		}
		if (sourUserProfileType.Email.exists()) {
			destUserProfileType.Email.setValue(sourUserProfileType.Email.getValue());
		}
		if (sourUserProfileType.Gender.exists()) {
			destUserProfileType.Gender.setValue(sourUserProfileType.Gender.getValue());
		}
		if (sourUserProfileType.ID.exists()) {
			destUserProfileType.ID.setValue(sourUserProfileType.ID.getValue());
		}
		if (sourUserProfileType.Password.exists()) {
			destUserProfileType.Password.setValue(sourUserProfileType.Password.getValue());
		}
		if (sourUserProfileType.AccessCode.exists()) {
			destUserProfileType.AccessCode.setValue(sourUserProfileType.AccessCode.getValue());
		}
	}

	/**
	 * Copy down from the Server element
	 *
	 * @param destServer
	 * @param sourServer
	 */
	public static void copyServerType(ServerType destServer, ServerType sourServer) {

		if (sourServer.Request.exists()) {
			copyRequestTypeList(destServer, sourServer);
		}

		if (sourServer.Response.exists()) {
			copyResponseTypeList(destServer, sourServer);
		}
	}

	/**
	 * Copy down from the Request element
	 *
	 * @param destServer
	 * @param sourServer
	 */
	public static void copyRequestTypeList(ServerType destServer, ServerType sourServer) {
		if (sourServer.Request.exists()) {
			for (int i = 0; i < sourServer.Request.count(); i++) {
				copyRequestType(destServer.Request.append(), sourServer.Request.at(i));
			}
		}
	}

	/**
	 * Copy under the Request element
	 *
	 * @param destRequestType
	 * @param sourRequestType
	 */
	public static void copyRequestType(RequestType destRequestType, RequestType sourRequestType) {
		if (sourRequestType.ProcessOrder.exists()) {
			destRequestType.ProcessOrder.setValue(sourRequestType.ProcessOrder.getValue());
		}
		if (sourRequestType.Service.exists()) {
			destRequestType.Service.setValue(sourRequestType.Service.getValue());
		}

		copyInputTypeList(destRequestType, sourRequestType);

		if (sourRequestType.InputUserProfile.exists()) {
			for (int i = 0; i < sourRequestType.InputUserProfile.count(); i++) {
				InputUserProfileType sourInputUserProfileType = sourRequestType.InputUserProfile.at(i);
				InputUserProfileType destInputUserProfileType = destRequestType.InputUserProfile.append();

				copyInputUserProfileType(destInputUserProfileType, sourInputUserProfileType);
			}
		}
		if (sourRequestType.TargetOutput.exists()) {
			for (int i = 0; i < sourRequestType.TargetOutput.count(); i++) {
				TargetOutputType sourTargetOutputType = sourRequestType.TargetOutput.at(i);
				TargetOutputType destTargetOutputType = destRequestType.TargetOutput.append();

				copyTargetOutputType(destTargetOutputType, sourTargetOutputType);
			}
		}
		if (sourRequestType.Routing.exists()) {
			for (int i = 0; i < sourRequestType.Routing.count(); i++) {
				RoutingType sourRoutingType = sourRequestType.Routing.at(i);
				RoutingType destRoutingType = destRequestType.Routing.append();

				copyRoutingType(destRoutingType, sourRoutingType);
			}
		}

	}

	/**
	 * Copy under the TargetOutput element
	 *
	 * @param destTargetOutputType
	 * @param sourTargetOutputType
	 */
	public static void
			copyTargetOutputType(TargetOutputType destTargetOutputType, TargetOutputType sourTargetOutputType) {
		if (sourTargetOutputType.HypothesisFormat.exists()) {
			for (int i = 0; i < sourTargetOutputType.HypothesisFormat.count(); i++) {
				HypothesisFormatType sourHypothesisFormatType = sourTargetOutputType.HypothesisFormat.at(i);
				HypothesisFormatType destHypothesisFormatType = destTargetOutputType.HypothesisFormat.append();

				if (sourHypothesisFormatType.NofN_best.exists()) {
					destHypothesisFormatType.NofN_best.setValue(sourHypothesisFormatType.NofN_best.getValue());
				}
			}
		}
		if (sourTargetOutputType.LanguageType2.exists()) {
			for (int i = 0; i < sourTargetOutputType.LanguageType2.count(); i++) {
				LanguageTypeType sourLanguageTypeType = sourTargetOutputType.LanguageType2.at(i);
				LanguageTypeType destLanguageTypeType = destTargetOutputType.LanguageType2.append();

				if (sourLanguageTypeType.Dialect.exists()) {
					destLanguageTypeType.Dialect.setValue(sourLanguageTypeType.Dialect.getValue());
				}
				if (sourLanguageTypeType.ID.exists()) {
					destLanguageTypeType.ID.setValue(sourLanguageTypeType.ID.getValue());
				}
			}
		}
		if (sourTargetOutputType.Option.exists()) {
			for (int i = 0; i < sourTargetOutputType.Option.count(); i++) {
				OptionType sourOptionType = sourTargetOutputType.Option.at(i);
				OptionType destOptionType = destTargetOutputType.Option.append();

				if (sourOptionType.Key.exists()) {
					destOptionType.Key.setValue(sourOptionType.Key.getValue());
				}
				if (sourOptionType.Value2.exists()) {
					destOptionType.Value2.setValue(sourOptionType.Value2.getValue());
				}
			}
		}
	}

	/**
	 * Copy under the InputUserProfile element
	 *
	 * @param destInputUserProfileType
	 * @param sourInputUserProfileType
	 */
	public static void copyInputUserProfileType(InputUserProfileType destInputUserProfileType,
			InputUserProfileType sourInputUserProfileType) {
		if (sourInputUserProfileType.Age.exists()) {
			destInputUserProfileType.Age.setValue(sourInputUserProfileType.Age.getValue());
		}
		if (sourInputUserProfileType.Email.exists()) {
			destInputUserProfileType.Email.setValue(sourInputUserProfileType.Email.getValue());
		}
		if (sourInputUserProfileType.Gender.exists()) {
			destInputUserProfileType.Gender.setValue(sourInputUserProfileType.Gender.getValue());
		}
		if (sourInputUserProfileType.ID.exists()) {
			destInputUserProfileType.ID.setValue(sourInputUserProfileType.ID.getValue());
		}
		if (sourInputUserProfileType.InputModality.exists()) {
			for (int i = 0; i < sourInputUserProfileType.InputModality.count(); i++) {
				InputModalityType sourInputModalityType = sourInputUserProfileType.InputModality.at(i);
				InputModalityType destInputModalityType = destInputUserProfileType.InputModality.append();

				copyInputModalityType(destInputModalityType, sourInputModalityType);
			}
		}
		if (sourInputUserProfileType.Password.exists()) {
			destInputUserProfileType.Password.setValue(sourInputUserProfileType.Password.getValue());
		}
		if (sourInputUserProfileType.AccessCode.exists()) {
			destInputUserProfileType.AccessCode.setValue(sourInputUserProfileType.AccessCode.getValue());
		}
	}

	/**
	 * Copy under the InputModality elements<br>
	 *
	 * Do not use the Signing element.
	 *
	 * @param destInputModalityType
	 * @param sourInputModalityType
	 */
	public static void copyInputModalityType(InputModalityType destInputModalityType,
			InputModalityType sourInputModalityType) {
		if (sourInputModalityType.Signing.exists()) {
			for (int i = 0; i < sourInputModalityType.Signing.count(); i++) {
//				SigningType sourSigningType = sourInputModalityType.Signing.at(i);
//				SigningType destSigningType = destInputModalityType.Signing.append();

				log.error("This element type is  the any. You must to create an element in XMLSpy.(Feature)");
				// TODO It is a anyType element
			}
		}
		if (sourInputModalityType.Speaking.exists()) {
			for (int i = 0; i < sourInputModalityType.Speaking.count(); i++) {
				SpeakingType sourSpeakingType = sourInputModalityType.Speaking.at(i);
				SpeakingType destSpeakingType = destInputModalityType.Speaking.append();

				copySpeakingType(destSpeakingType, sourSpeakingType);
			}
		}
		if (sourInputModalityType.Writing.exists()) {
			for (int i = 0; i < sourInputModalityType.Writing.count(); i++) {
				WritingType sourWritingType = sourInputModalityType.Writing.at(i);
				WritingType destWritingType = destInputModalityType.Writing.append();

				copyWritingType(destWritingType, sourWritingType);
			}
		}
	}

	/**
	 * Copy under the Speaking element
	 *
	 * @param destSpeakingType
	 * @param sourSpeakingType
	 */
	public static void copySpeakingType(SpeakingType destSpeakingType, SpeakingType sourSpeakingType) {
		if (sourSpeakingType.Language.exists()) {
			for (int i = 0; i < sourSpeakingType.Language.count(); i++) {
				LanguageType sourLanguageType = sourSpeakingType.Language.at(i);
				LanguageType destLanguageType = destSpeakingType.Language.append();

				copyLanguageType(destLanguageType, sourLanguageType);
			}
		}
	}

	/**
	 * Copy under the Writing element
	 *
	 * @param destWritingType
	 * @param sourWritingType
	 */
	public static void copyWritingType(WritingType destWritingType, WritingType sourWritingType) {
		if (sourWritingType.Language.exists()) {
			for (int i = 0; i < sourWritingType.Language.count(); i++) {
				LanguageType sourLanguageType = sourWritingType.Language.at(i);
				LanguageType destLanguageType = destWritingType.Language.append();

				copyLanguageType(destLanguageType, sourLanguageType);
			}
		}
	}

	/**
	 * Copy under the element
	 *
	 * @param destLanguageType
	 * @param sourLanguageType
	 */
	public static void copyLanguageType(LanguageType destLanguageType, LanguageType sourLanguageType) {
		if (sourLanguageType.Dialect.exists()) {
			destLanguageType.Dialect.setValue(sourLanguageType.Dialect.getValue());
		}
		if (sourLanguageType.Fluency.exists()) {
			destLanguageType.Fluency.setValue(sourLanguageType.Fluency.getValue());
		}
		if (sourLanguageType.ID.exists()) {
			destLanguageType.ID.setValue(sourLanguageType.ID.getValue());
		}
	}

	/**
	 * Copy under the element
	 *
	 * @param destRequest
	 * @param sourRequest
	 */
	public static void copyInputTypeList(RequestType destRequest, RequestType sourRequest) {
		if (sourRequest.Input.exists()) {
			for (int i = 0; i < sourRequest.Input.count(); i++) {
				InputType sourInputType = sourRequest.Input.at(i);
				InputType destInputType = destRequest.Input.append();

				copyInputType(destInputType, sourInputType);
			}
		}

	}

	/**
	 * Copy under the  element
	 *
	 * @param destInputType
	 * @param sourInputType
	 */
	public static void copyInputType(InputType destInputType, InputType sourInputType) {
		if (sourInputType.AttachedBinary.exists()) {
			for (int i = 0; i < sourInputType.AttachedBinary.count(); i++) {
				AttachedBinaryType sourAttachedBinaryType = sourInputType.AttachedBinary.at(i);
				AttachedBinaryType destAttachedBinaryType = destInputType.AttachedBinary.append();

				copyAttachedBinaryType(destAttachedBinaryType, sourAttachedBinaryType);
			}
		}
		if (sourInputType.Data.exists()) {
			for (int i = 0; i < sourInputType.Data.count(); i++) {
				DataType sourDataType = sourInputType.Data.at(i);
				DataType destDataType = destInputType.Data.append();

				copyDataType(destDataType, sourDataType);
			}
		}
	}

	/**
	 * Copy down from the Response element
	 *
	 * @param destServer
	 * @param sourServer
	 */
	public static void copyResponseTypeList(ServerType destServer, ServerType sourServer) {
		if (sourServer.Response.exists()) {
			for (int i = 0; i < sourServer.Response.count(); i++) {
				copyResponseType(destServer.Response.append(), sourServer.Response.at(i));
			}
		}
	}

	/**
	 * Copy under the Response element
	 *
	 * @param destResponseType
	 * @param sourResponseType
	 */
	public static void copyResponseType(ResponseType destResponseType, ResponseType sourResponseType) {
		if (sourResponseType.ProcessOrder.exists()) {
			destResponseType.ProcessOrder.setValue(sourResponseType.ProcessOrder.getValue());
		}
		if (sourResponseType.Service.exists()) {
			destResponseType.Service.setValue(sourResponseType.Service.getValue());
		}
		if (sourResponseType.Error.exists()) {
			for (int i = 0; i < sourResponseType.Error.count(); i++) {
				ErrorType sourErrorType = sourResponseType.Error.at(i);
				ErrorType destErrorType = destResponseType.Error.append();

				if (sourErrorType.Code.exists()) {
					destErrorType.Code.setValue(sourErrorType.Code.getValue());
				}
				if (sourErrorType.Message.exists()) {
					destErrorType.Message.setValue(sourErrorType.Message.getValue());
				}
				if (sourErrorType.Service.exists()) {
					destErrorType.Service.setValue(sourErrorType.Service.getValue());
				}
			}
		}

		copyOutputTypeList(destResponseType, sourResponseType);

		if (sourResponseType.Routing.exists()) {
			for (int i = 0; i < sourResponseType.Routing.count(); i++) {
				RoutingType sourRoutingType = sourResponseType.Routing.at(i);
				RoutingType destRoutingType = destResponseType.Routing.append();

				copyRoutingType(destRoutingType, sourRoutingType);
			}
		}
	}

	/**
	 * Copy under the Routing element
	 *
	 * @param destRoutingType
	 * @param sourRoutingType
	 */
	public static void copyRoutingType(RoutingType destRoutingType, RoutingType sourRoutingType) {
		if (sourRoutingType.From.exists()) {
			for (int j = 0; j < sourRoutingType.From.count(); j++) {
				FromType sourFromType = sourRoutingType.From.at(j);
				FromType destFromType = destRoutingType.From.append();

				if (sourFromType.URI.exists()) {
					destFromType.URI.setValue(sourFromType.URI.getValue());
				}
			}
		}
		if (sourRoutingType.To.exists()) {
			for (int j = 0; j < sourRoutingType.To.count(); j++) {
				ToType sourToType = sourRoutingType.To.at(j);
				ToType destToType = destRoutingType.To.append();

				if (sourToType.URI.exists()) {
					destToType.URI.setValue(sourToType.URI.getValue());
				}
			}
		}
	}

	/**
	 * Copy down from the Output element
	 *
	 * @param destResponse
	 * @param sourResponse
	 */
	public static void copyOutputTypeList(ResponseType destResponse, ResponseType sourResponse) {
		if (sourResponse.Output.exists()) {
			for (int i = 0; i < sourResponse.Output.count(); i++) {
				OutputType sourOutputType = sourResponse.Output.at(i);
				OutputType destOutputType = destResponse.Output.append();

				if (sourOutputType.AttachedBinary.exists()) {
					for (int j = 0; j < sourOutputType.AttachedBinary.count(); j++) {
						AttachedBinaryType sourAttachedBinaryType = sourOutputType.AttachedBinary.at(j);
						AttachedBinaryType destAttachedBinaryType = destOutputType.AttachedBinary.append();

						copyAttachedBinaryType(destAttachedBinaryType, sourAttachedBinaryType);
					}
				}
				copyDataTypeList(destOutputType, sourOutputType);
			}
		}
	}

	/**
	 * Copy under the AttachedBinary element
	 *
	 * @param destAttachedBinaryType
	 * @param sourAttachedBinaryType
	 */
	public static void copyAttachedBinaryType(AttachedBinaryType destAttachedBinaryType,
			AttachedBinaryType sourAttachedBinaryType) {

		if (sourAttachedBinaryType.ChannelID.exists()) {
			destAttachedBinaryType.ChannelID.setValue(sourAttachedBinaryType.ChannelID.getValue());
		}

		if (sourAttachedBinaryType.DataID.exists()) {
			destAttachedBinaryType.DataID.setValue(sourAttachedBinaryType.DataID.getValue());
		}

		if (sourAttachedBinaryType.DataType2.exists()) {
			destAttachedBinaryType.DataType2.setValue(sourAttachedBinaryType.DataType2.getValue());
		}
	}

	/**
	 * Copy down from the DataType element
	 *
	 * @param destOutputType
	 * @param sourOutputType
	 */
	public static void copyDataTypeList(OutputType destOutputType, OutputType sourOutputType) {
		if (sourOutputType.Data.exists()) {
			for (int i = 0; i < sourOutputType.Data.count(); i++) {
				DataType sourDataType = sourOutputType.Data.at(i);
				DataType destDataType = destOutputType.Data.append();

				copyDataType(destDataType, sourDataType);
			}
		}

	}

	/**
	 * Copy under the DataType element
	 *
	 * @param destDataType
	 * @param sourDataType
	 */
	public static void copyDataType(DataType destDataType, DataType sourDataType) {
		if (sourDataType.Audio.exists()) {
			for (int i = 0; i < sourDataType.Audio.count(); i++) {
				AudioType sourAudioType = sourDataType.Audio.at(i);
				AudioType destAudioType = destDataType.Audio.append();

				copyAudioType(destAudioType, sourAudioType);
			}
		}
		if (sourDataType.Image.exists()) {
			for (int i = 0; i < sourDataType.Image.count(); i++) {
				ImageType sourImageType = sourDataType.Image.at(i);
				ImageType destImageType = destDataType.Image.append();

				if (sourImageType.BeginTimestamp.exists()) {
					destImageType.BeginTimestamp.setValue(sourImageType.BeginTimestamp.getValue());
				}
				if (sourImageType.ChannelID.exists()) {
					destImageType.ChannelID.setValue(sourImageType.ChannelID.getValue());
				}
				if (sourImageType.EndPoint.exists()) {
					destImageType.EndPoint.setValue(sourImageType.EndPoint.getValue());
				}
				if (sourImageType.EndTimestamp.exists()) {
					destImageType.EndTimestamp.setValue(sourImageType.EndTimestamp.getValue());
				}
				if (sourImageType.ModelType.exists()) {
					for (int cntModelType = 0; cntModelType < sourImageType.ModelType.count(); cntModelType++) {
						ModelTypeType sourModelTypeType = sourImageType.ModelType.at(cntModelType);
						ModelTypeType destModelTypeType = sourImageType.ModelType.append();

						copyModelTypeType(destModelTypeType, sourModelTypeType);
					}
				}

			}
		}
		if (sourDataType.Text.exists()) {
			for (int i = 0; i < sourDataType.Text.count(); i++) {
				TextType sourTextType = sourDataType.Text.at(i);
				TextType destTextType = destDataType.Text.append();

				copyTextType(destTextType, sourTextType);
			}
		}
		if (sourDataType.Video.exists()) {
			for (int i = 0; i < sourDataType.Video.count(); i++) {
				VideoType sourVideoType = sourDataType.Video.at(i);
				VideoType destVideoType = destDataType.Video.append();

				if (sourVideoType.BeginTimestamp.exists()) {
					destVideoType.BeginTimestamp.setValue(sourVideoType.BeginTimestamp.getValue());
				}
				if (sourVideoType.ChannelID.exists()) {
					destVideoType.ChannelID.setValue(sourVideoType.ChannelID.getValue());
				}
				if (sourVideoType.EndPoint.exists()) {
					destVideoType.EndPoint.setValue(sourVideoType.EndPoint.getValue());
				}
				if (sourVideoType.EndTimestamp.exists()) {
					destVideoType.EndTimestamp.setValue(sourVideoType.EndTimestamp.getValue());
				}

				if (sourVideoType.ModelType.exists()) {
					for (int j = 0; j < sourVideoType.ModelType.count(); j++) {
						ModelTypeType sourModelTypeType = sourVideoType.ModelType.at(j);
						ModelTypeType destModelTypeType = destVideoType.ModelType.append();

						copyModelTypeType(destModelTypeType, sourModelTypeType);
					}
				}
			}
		}
		if (sourDataType.URL.exists()) {
			destDataType.URL.append().setValue(sourDataType.URL.first().getValue());
		}

		if (sourDataType.Custom.exists()) {
			for (int i = 0; i < sourDataType.Custom.count(); i++) {
				CustomType sourCustomType = sourDataType.Custom.at(i);
				CustomType destCustomType = destDataType.Custom.append();

				copyCustomType(destCustomType, sourCustomType);
			}
		}
	}

	/**
	 * to copy under the Audio element<br>
	 *
	 * Do not use the Feature element.
	 *
	 *
	 * @param destAudioType
	 * @param sourAudioType
	 */
	public static void copyAudioType(AudioType destAudioType, AudioType sourAudioType) {
		if (sourAudioType.BeginTimestamp.exists()) {
			destAudioType.BeginTimestamp.setValue(sourAudioType.BeginTimestamp.getValue());
		}
		if (sourAudioType.ChannelID.exists()) {
			destAudioType.ChannelID.setValue(sourAudioType.ChannelID.getValue());
		}
		if (sourAudioType.EndPoint.exists()) {
			destAudioType.EndPoint.setValue(sourAudioType.EndPoint.getValue());
		}
		if (sourAudioType.EndTimestamp.exists()) {
			destAudioType.EndTimestamp.setValue(sourAudioType.EndTimestamp.getValue());
		}
		if (sourAudioType.Feature.exists()) {

			for (int i = 0; i < sourAudioType.Feature.count(); i++) {

//				FeatureType sourFeatureType = sourAudioType.Feature.at(i);
//				FeatureType destFeatureType = destAudioType.Feature.append();

				log.error("This element has the anyType attribute. You must to create an element in XMLSpy.(Feature)");
				// TODO It is a anyType attribute.

			}
		}
		if (sourAudioType.ModelType.exists()) {
			for (int i = 0; i < sourAudioType.ModelType.count(); i++) {
				ModelTypeType sourModelTypeType = sourAudioType.ModelType.at(i);
				ModelTypeType destModelTypeType = destAudioType.ModelType.append();

				copyModelTypeType(destModelTypeType, sourModelTypeType);
			}
		}
		if (sourAudioType.Signal.exists()) {
			for (int i = 0; i < sourAudioType.Signal.count(); i++) {
				SignalType sourSignalType = sourAudioType.Signal.at(i);
				SignalType destSignalType = destAudioType.Signal.append();

				if (sourSignalType.AudioFormat.exists()) {
					destSignalType.AudioFormat.setValue(sourSignalType.AudioFormat.getValue());
				}
				if (sourSignalType.BitRate.exists()) {
					destSignalType.BitRate.setValue(sourSignalType.BitRate.getValue());
				}
				if (sourSignalType.ChannelQty.exists()) {
					destSignalType.ChannelQty.setValue(sourSignalType.ChannelQty.getValue());
				}
				if (sourSignalType.Endian.exists()) {
					destSignalType.Endian.setValue(sourSignalType.Endian.getValue());
				}
				if (sourSignalType.SamplingRate.exists()) {
					destSignalType.SamplingRate.setValue(sourSignalType.SamplingRate.getValue());
				}
				if (sourSignalType.ValueType.exists()) {
					destSignalType.ValueType.setValue(sourSignalType.ValueType.getValue());
				}
			}
		}

	}

	/**
	 * Copy under the Text element
	 *
	 * @param destTextType
	 * @param sourTextType
	 */
	public static void copyTextType(TextType destTextType, TextType sourTextType) {

		if (sourTextType.BeginTimestamp.exists()) {
			destTextType.BeginTimestamp.setValue(sourTextType.BeginTimestamp.getValue());
		}
		if (sourTextType.ChannelID.exists()) {
			destTextType.ChannelID.setValue(sourTextType.ChannelID.getValue());
		}
		if (sourTextType.EndPoint.exists()) {
			destTextType.EndPoint.setValue(sourTextType.EndPoint.getValue());
		}
		if (sourTextType.EndTimestamp.exists()) {
			destTextType.EndTimestamp.setValue(sourTextType.EndTimestamp.getValue());
		}
		if (sourTextType.ModelType.exists()) {
			for (int i = 0; i < sourTextType.ModelType.count(); i++) {
				ModelTypeType sourModelTypeType = sourTextType.ModelType.at(i);
				ModelTypeType destModelTypeType = destTextType.ModelType.append();

				copyModelTypeType(destModelTypeType, sourModelTypeType);
			}
		}
		if (sourTextType.SentenceSequence.exists()) {
			for (int i = 0; i < sourTextType.SentenceSequence.count(); i++) {
				SentenceSequenceType sourSentenceSequenceType = sourTextType.SentenceSequence.at(i);
				SentenceSequenceType destSentenceSequenceType = destTextType.SentenceSequence.append();

				copySentenceSequenceType(destSentenceSequenceType, sourSentenceSequenceType);

			}
		}
	}

	/**
	 * Copy under the SentenceSequence element
	 *
	 * @param destSentenceSequenceType
	 * @param sourSentenceSequenceType
	 */
	public static void copySentenceSequenceType(SentenceSequenceType destSentenceSequenceType,
			SentenceSequenceType sourSentenceSequenceType) {

		if (sourSentenceSequenceType.BeginTime.exists()) {
			destSentenceSequenceType.BeginTime.setValue(sourSentenceSequenceType.BeginTime.getValue());
		}
		if (sourSentenceSequenceType.EndTime.exists()) {
			destSentenceSequenceType.EndTime.setValue(sourSentenceSequenceType.EndTime.getValue());
		}
		if (sourSentenceSequenceType.N_bestRank.exists()) {
			destSentenceSequenceType.N_bestRank.setValue(sourSentenceSequenceType.N_bestRank.getValue());
		}
		if (sourSentenceSequenceType.Order.exists()) {
			destSentenceSequenceType.Order.setValue(sourSentenceSequenceType.Order.getValue());
		}
		if (sourSentenceSequenceType.Score.exists()) {
			destSentenceSequenceType.Score.setValue(sourSentenceSequenceType.Score.getValue());
		}
		if (sourSentenceSequenceType.Sentence.exists()) {
			for (int i = 0; i < sourSentenceSequenceType.Sentence.count(); i++) {
				SentenceType sourSentenceType = sourSentenceSequenceType.Sentence.at(i);
				SentenceType destSentenceType = destSentenceSequenceType.Sentence.append();

				copySentenceType(destSentenceType, sourSentenceType);
			}
		}
		if (sourSentenceSequenceType.TimeStamp.exists()) {
			destSentenceSequenceType.TimeStamp.setValue(sourSentenceSequenceType.TimeStamp.getValue());
		}

	}

	/**
	 * Copy under the Sentence element
	 *
	 * @param destSentenceType
	 * @param sourSentenceType
	 */
	public static void copySentenceType(SentenceType destSentenceType, SentenceType sourSentenceType) {
		if (sourSentenceType.BeginTime.exists()) {
			destSentenceType.BeginTime.setValue(sourSentenceType.BeginTime.getValue());
		}
		if (sourSentenceType.Chunk.exists()) {
			for (int i = 0; i < sourSentenceType.Chunk.count(); i++) {
				ChunkType sourChunkType = sourSentenceType.Chunk.at(i);
				ChunkType destChunkType = destSentenceType.Chunk.append();

				copyChunkType(destChunkType, sourChunkType);
			}
		}
		if (sourSentenceType.EndTime.exists()) {
			destSentenceType.EndTime.setValue(sourSentenceType.EndTime.getValue());
		}
		if (sourSentenceType.Function.exists()) {
			for (int i = 0; i < sourSentenceType.Function.count(); i++) {
				destSentenceType.Function.append().setValue(sourSentenceType.Function.at(i).getValue());
			}
		}
		if (sourSentenceType.Order.exists()) {
			destSentenceType.Order.setValue(sourSentenceType.Order.getValue());
		}
		if (sourSentenceType.Score.exists()) {
			destSentenceType.Score.setValue(sourSentenceType.Score.getValue());
		}
		if (sourSentenceType.Surface.exists()) {
			for (int i = 0; i < sourSentenceType.Surface.count(); i++) {
				SurfaceType2 sourSurfaceType = sourSentenceType.Surface.at(i);
				SurfaceType2 destSurfaceType = destSentenceType.Surface.append();

				destSurfaceType.setValue(sourSurfaceType.getValue());
				if (sourSurfaceType.Delimiter.exists()) {
					destSurfaceType.Delimiter.setValue(sourSurfaceType.Delimiter.getValue());
				}
			}
		}
		if (sourSentenceType.TimeStamp.exists()) {
			destSentenceType.TimeStamp.setValue(sourSentenceType.TimeStamp.getValue());
		}
	}

	/**
	 * Copy under the Chunk element
	 *
	 * @param destChunkType
	 * @param sourChunkType
	 */
	public static void copyChunkType(ChunkType destChunkType, ChunkType sourChunkType) {
		if (sourChunkType.Accent.exists()) {
			for (int i = 0; i < sourChunkType.Accent.count(); i++) {
				AccentType sourAccentType = sourChunkType.Accent.at(i);
				AccentType destAccentType = destChunkType.Accent.append();

				if (sourAccentType.DictionaryID.exists()) {
					destAccentType.DictionaryID.setValue(sourAccentType.DictionaryID.getValue());
				}
				if (sourAccentType.EntryID.exists()) {
					destAccentType.EntryID.setValue(sourAccentType.EntryID.getValue());
				}
			}
		}
		if (sourChunkType.BeginTime.exists()) {
			destChunkType.BeginTime.setValue(sourChunkType.BeginTime.getValue());
		}
		if (sourChunkType.EndTime.exists()) {
			destChunkType.EndTime.setValue(sourChunkType.EndTime.getValue());

		}
		if (sourChunkType.IPA.exists()) {
			for (int i = 0; i < sourChunkType.IPA.count(); i++) {
				IPAType sourIPAType = sourChunkType.IPA.at(i);
				IPAType destIPAType = destChunkType.IPA.append();

				if (sourIPAType.DictionaryID.exists()) {
					destIPAType.DictionaryID.setValue(sourIPAType.DictionaryID.getValue());
				}
				if (sourIPAType.EntryID.exists()) {
					destIPAType.EntryID.setValue(sourIPAType.EntryID.getValue());
				}
			}
		}
		if (sourChunkType.Order.exists()) {
			destChunkType.Order.setValue(sourChunkType.Order.getValue());
		}
		if (sourChunkType.POS.exists()) {
			for (int i = 0; i < sourChunkType.POS.count(); i++) {
				POSType sourPOSType = sourChunkType.POS.at(i);
				POSType destPOSType = destChunkType.POS.append();

				destPOSType.setValue(sourPOSType.getValue());
				if (sourPOSType.DictionaryID.exists()) {
					destPOSType.DictionaryID.setValue(sourPOSType.DictionaryID.getValue());
				}
				if (sourPOSType.EntryID.exists()) {
					destPOSType.EntryID.setValue(sourPOSType.EntryID.getValue());
				}
			}
		}
		if (sourChunkType.Pronunciation.exists()) {
			for (int i = 0; i < sourChunkType.Pronunciation.count(); i++) {
				PronunciationType sourPronunciationType = sourChunkType.Pronunciation.at(i);
				PronunciationType destPronunciationType = destChunkType.Pronunciation.append();

				if (sourPronunciationType.DictionaryID.exists()) {
					destPronunciationType.DictionaryID.setValue(sourPronunciationType.DictionaryID.getValue());
				}
				if (sourPronunciationType.EntryID.exists()) {
					destPronunciationType.EntryID.setValue(sourPronunciationType.EntryID.getValue());
				}
			}
		}
		if (sourChunkType.Score.exists()) {
			destChunkType.Score.setValue(sourChunkType.Score.getValue());
		}
		if (sourChunkType.Surface.exists()) {
			for (int i = 0; i < sourChunkType.Surface.count(); i++) {
				SurfaceType sourSurfaceType = sourChunkType.Surface.at(i);
				SurfaceType destSurfaceType = destChunkType.Surface.append();

				destSurfaceType.setValue(sourSurfaceType.getValue());
				if (sourSurfaceType.DictionaryID.exists()) {
					destSurfaceType.DictionaryID.setValue(sourSurfaceType.DictionaryID.getValue());
				}
				if (sourSurfaceType.EntryID.exists()) {
					destSurfaceType.EntryID.setValue(sourSurfaceType.EntryID.getValue());
				}
			}
		}
		if (sourChunkType.TimeStamp.exists()) {
			destChunkType.TimeStamp.setValue(sourChunkType.TimeStamp.getValue());
		}
	}

	/**
	 * Copy under the ModelType element
	 *
	 * @param destModelTypeType
	 * @param sourModelTypeType
	 */
	public static void copyModelTypeType(ModelTypeType destModelTypeType, ModelTypeType sourModelTypeType) {
		if (sourModelTypeType.Domain.exists()) {
			for (int i = 0; i < sourModelTypeType.Domain.count(); i++) {
				destModelTypeType.Domain.append().setValue(sourModelTypeType.Domain.at(i).getValue());
			}
		}
		if (sourModelTypeType.Language.exists()) {
			for (int i = 0; i < sourModelTypeType.Language.count(); i++) {
				LanguageType sourLanguageType = sourModelTypeType.Language.at(i);
				LanguageType destLanguageType = destModelTypeType.Language.append();

				if (sourLanguageType.Dialect.exists()) {
					destLanguageType.Dialect.setValue(sourLanguageType.Dialect.getValue());
				}
				if (sourLanguageType.Fluency.exists()) {
					destLanguageType.Fluency.setValue(sourLanguageType.Fluency.getValue());
				}
				if (sourLanguageType.ID.exists()) {
					destLanguageType.ID.setValue(sourLanguageType.ID.getValue());
				}
			}
		}
		if (sourModelTypeType.Personality.exists()) {
			for (int i = 0; i < sourModelTypeType.Personality.count(); i++) {
				PersonalityType sourPersonalityType = sourModelTypeType.Personality.at(i);
				PersonalityType destPersonalityType = destModelTypeType.Personality.append();

				if (sourPersonalityType.Age.exists()) {
					destPersonalityType.Age.setValue(sourPersonalityType.Age.getValue());
				}
				if (sourPersonalityType.Gender.exists()) {
					destPersonalityType.Gender.setValue(sourPersonalityType.Gender.getValue());
				}
				if (sourPersonalityType.ID.exists()) {
					destPersonalityType.ID.setValue(sourPersonalityType.ID.getValue());
				}
			}
		}
		if (sourModelTypeType.Task.exists()) {
			for (int i = 0; i < sourModelTypeType.Task.count(); i++) {
				destModelTypeType.Task.append().setValue(sourModelTypeType.Task.at(i).getValue());
			}
		}
	}

	public static void copyHistoryTypeList(MCMLType destMCMLType, MCMLType sourMCMLType) {
		if (!sourMCMLType.History.exists()) {
			return;
		}

		for (int i = 0; i < sourMCMLType.History.count(); i++) {
			HistoryType sourHistoryType = sourMCMLType.History.at(i);
			HistoryType destHistoryType = destMCMLType.History.append();

			copyHistoryType(destHistoryType, sourHistoryType);
		}
	}

	public static void copyHistoryType(HistoryType destHistoryType, HistoryType sourHistoryType) {
		if (sourHistoryType.Request.exists()) {

			for (int i = 0; i < sourHistoryType.Request.count(); i++) {
				RequestType sourRequestType = sourHistoryType.Request.at(i);
				RequestType destRequestType = destHistoryType.Request.append();

				copyRequestType(destRequestType, sourRequestType);
			}
		}
		if (sourHistoryType.Response.exists()) {

			for (int i = 0; i < sourHistoryType.Response.count(); i++) {
				ResponseType sourResponseType = sourHistoryType.Response.at(i);
				ResponseType destResponseType = destHistoryType.Response.append();

				copyResponseType(destResponseType, sourResponseType);
			}
		}
	}

	/**
	 * Copy under the Custom element
	 *
	 * @param destTextType
	 * @param sourTextType
	 */
	private static void copyCustomType(CustomType destCustomType, CustomType sourCustomType) {
		if (sourCustomType.Detail.exists()) {
			DetailType sourDetailType = sourCustomType.Detail.at(0);
			DetailType destDetailType = destCustomType.Detail.append();
			copyDetailType(destDetailType, sourDetailType);
		} else if (sourCustomType.List.exists()) {
			ListType sourListType = sourCustomType.List.at(0);
			ListType destListType = destCustomType.List.append();
			copyListType(destListType, sourListType);
		} else if (sourCustomType.Map.exists()) {
			MapType sourMapType = sourCustomType.Map.at(0);
			MapType destMapType = destCustomType.Map.append();
			copyMapType(destMapType, sourMapType);
		}
	}

	/**
	 * Copy under the Detail element
	 *
	 * @param destDetailType
	 * @param sourDetailType
	 */
	private static void copyDetailType(DetailType destDetailType, DetailType sourDetailType) {
		if (sourDetailType.Title.exists()) {
			destDetailType.Title.append().setValue(sourDetailType.Title.first().getValue());
		}

		if (sourDetailType.Spot.exists()) {
			destDetailType.Spot.append().setValue(sourDetailType.Spot.first().getValue());
		}

		if (sourDetailType.Display.exists()) {
			destDetailType.Display.append().setValue(sourDetailType.Display.first().getValue());
		}

		if (sourDetailType.Image.exists()) {
			destDetailType.Image.append().setValue(sourDetailType.Image.first().getValue());
		}
	}

	/**
	 * Copy under the List element
	 *
	 * @param destListType
	 * @param sourListType
	 */
	private static void copyListType(ListType destListType, ListType sourListType) {
		if (sourListType.Title.exists()) {
			destListType.Title.append().setValue(sourListType.Title.first().getValue());
		}

		if (sourListType.Item.exists()) {
			for (int i = 0; i < sourListType.Item.count(); i++) {
				ItemType sourItemType = sourListType.Item.at(i);
				ItemType destItemType = destListType.Item.append();

				if (sourItemType.Spot.exists()) {
					destItemType.Spot.append().setValue(sourItemType.Spot.first().getValue());
				}

				if (sourItemType.Image.exists()) {
					destItemType.Image.append().setValue(sourItemType.Image.first().getValue());
				}
			}
		}
	}

	/**
	 * Copy under the Map element
	 *
	 * @param destMapType
	 * @param sourMapType
	 */
	private static void copyMapType(MapType destMapType, MapType sourMapType) {
		if (sourMapType.Title.exists()) {
			destMapType.Title.append().setValue(sourMapType.Title.first().getValue());
		}

		if (sourMapType.Spot.exists()) {
			destMapType.Spot.append().setValue(sourMapType.Spot.first().getValue());
		}

		if (sourMapType.Latitude.exists()) {
			destMapType.Latitude.append().setValue(sourMapType.Latitude.first().getValue());
		}

		if (sourMapType.Longitude.exists()) {
			destMapType.Longitude.append().setValue(sourMapType.Longitude.first().getValue());
		}
	}
}
