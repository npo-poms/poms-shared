NEP api clients
====

As used by POMS.

NEP makes several API's available, which we make use of in POMS. It's a bit messy, we don't quite know which people are responsible for which API's nor how they relate to one another, and don't know of any publicly available documentation.

REST API
=====

NEPGatekeeperService
--
Intake for transcoding jobs. No call back. Poll NEPDownloadService

`nep.gatekeeper-api.baseUrl=https://npo-webonly-gatekeeper.nepworldwide.nl`


NEPItemizeService
--
Intake for 'itemize' and 'grab screen'.

`nep.itemizer-api.baseUrl=https://itemizer-npocdn-prd.nepworldwide.nl/v1`


NEPPlayerTokenService
--

widevine token, playready tokens

`nep.tokengenerator-api.baseUrl=http://tokengenerator-npo.cdn.streamgate.nl/keys-dev/token-provider/web/authenticate`


NEPSAMService
--

'Stream Access Management' API

`nep.sam-api.baseUrl=https://api.samgcloud.nepworldwide.nl/`

FTP Services
===
NEPUploadService
--
If you want to upload something for trancoding upload it here.

`nep.gatekeeper-upload.host=ftp.nepworldwide.nl`



NEPDownloadService
--
If you want to download transcoded item, or itemized item. (iirc)

`nep.itemizer-download.host=sftp-itemizer.nepworldwide.nl`
`
