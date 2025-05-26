import Flutter
import UIKit

class SegmentsVideoPlayerViewFactory: NSObject, FlutterPlatformViewFactory {
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?)
        -> FlutterPlatformView
    {
        return SegmentsVideoPlayerView(frame: frame)
    }
}
