import AVFoundation
import UIKit

class SegmentsVideoPlayerView: UIView {
    private var player: AVPlayer?
    private var playerLayer: AVPlayerLayer?
    private var timeObserverToken: Any?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupPlayer()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupPlayer()
    }

    private func setupPlayer() {
        player = AVPlayer()
        playerLayer = AVPlayerLayer(player: player)
        if let playerLayer = playerLayer {
            layer.addSublayer(playerLayer)
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        playerLayer?.frame = bounds
    }

    func initialize(with segments: [[String: Any]], width: Double, height: Double) {
        let composition: AVMutableComposition = AVMutableComposition()
        let videoComposition: AVMutableVideoComposition = AVMutableVideoComposition()
        var instructions: [AVMutableVideoCompositionInstruction] = []

        for segment: [String: Any] in segments {
            guard let path: String = segment["path"] as? String,
                let begin: Double = segment["begin"] as? Double,
                let end: Double = segment["end"] as? Double
            else { continue }

            let url = URL(fileURLWithPath: path)
            let asset = AVAsset(url: url)

            guard let videoAssetTrack: AVAssetTrack = asset.tracks(withMediaType: .video).first,
                let audioAssetTrack: AVAssetTrack = asset.tracks(withMediaType: .audio).first
            else { continue }

            let videoCompositionTrack: AVMutableCompositionTrack? = composition.addMutableTrack(
                withMediaType: .video, preferredTrackID: kCMPersistentTrackID_Invalid)
            let audioCompositionTrack: AVMutableCompositionTrack? = composition.addMutableTrack(
                withMediaType: .audio, preferredTrackID: kCMPersistentTrackID_Invalid)

            let timeRange: CMTimeRange = CMTimeRange(
                start: CMTime(seconds: begin, preferredTimescale: 600),
                end: CMTime(seconds: end, preferredTimescale: 600))

            try? videoCompositionTrack?.insertTimeRange(timeRange, of: videoAssetTrack, at: .zero)
            try? audioCompositionTrack?.insertTimeRange(timeRange, of: audioAssetTrack, at: .zero)

            let videoCompositionInstruction: AVMutableVideoCompositionInstruction =
                AVMutableVideoCompositionInstruction()
            videoCompositionInstruction.timeRange = timeRange

            let videoLayerInstruction: AVMutableVideoCompositionLayerInstruction =
                AVMutableVideoCompositionLayerInstruction(
                    assetTrack: videoAssetTrack)
            videoLayerInstruction.setTransform(videoAssetTrack.preferredTransform, at: .zero)

            videoCompositionInstruction.layerInstructions = [videoLayerInstruction]
            instructions.append(videoCompositionInstruction)
        }

        videoComposition.instructions = instructions
        videoComposition.renderSize = CGSize(width: width, height: height)
        videoComposition.frameDuration = CMTime(value: 1, timescale: 30)

        let playerItem: AVPlayerItem = AVPlayerItem(asset: composition)
        playerItem.videoComposition = videoComposition
        player?.replaceCurrentItem(with: playerItem)
        player?.volume = 0
    }

    func play() {
        player?.play()
        addPeriodicTimeObserver()
    }

    func pause() {
        player?.pause()
    }

    func stop() {
        player?.pause()
        player?.seek(to: .zero)
        removePeriodicTimeObserver()
    }

    func seekTo(position: CMTime) {
        player?.seek(to: position)
    }

    func dispose() {
        player?.pause()
        playerLayer?.removeFromSuperlayer()
        removePeriodicTimeObserver()
        player = nil
    }

    private func addPeriodicTimeObserver() {
        guard let player: AVPlayer = player else { return }

        timeObserverToken = player.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 1, preferredTimescale: 600),
            queue: DispatchQueue.main
        ) { [weak self] time in
            self?.onTimeUpdate(time: time)
        }
    }

    private func removePeriodicTimeObserver() {
        if let timeObserverToken: Any = timeObserverToken {
            player?.removeTimeObserver(timeObserverToken)
            self.timeObserverToken = nil
        }
    }

    private func onTimeUpdate(time: CMTime) {
        let position: Float64 = CMTimeGetSeconds(time)
        NotificationCenter.default.post(
            name: .playerPositionChanged, object: self, userInfo: ["position": position])
    }
}

extension Notification.Name {
    static let playerPositionChanged: Notification.Name = Notification.Name("playerPositionChanged")
}
