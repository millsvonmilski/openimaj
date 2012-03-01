/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.demos.sandbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openimaj.content.animation.AnimatedVideo;
import org.openimaj.demos.sandbox.asm.ASFDataset;
import org.openimaj.demos.sandbox.asm.ActiveShapeModel;
import org.openimaj.demos.sandbox.asm.ActiveShapeModel.IterationResult;
import org.openimaj.demos.sandbox.asm.landmark.NormalLandmarkModel;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.pixel.sampling.FLineSampler;
import org.openimaj.math.geometry.shape.PointDistributionModel;
import org.openimaj.math.geometry.shape.PointList;
import org.openimaj.math.geometry.shape.PointListConnections;
import org.openimaj.util.pair.IndependentPair;
import org.openimaj.video.VideoDisplay;

import Jama.Matrix;

public class ASMFitAnimation {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		final File dir = new File("/Users/jsh2/Work/lmlk/trunk/shared/JAAM-API/data/face-data");
		final File dir = new File("/Users/jsh2/Downloads/imm_face_db");
		ASFDataset dataset = new ASFDataset(dir);
		
		final List<IndependentPair<PointList, FImage>> data = dataset.getData();
		final PointListConnections conns = dataset.getConnections();

		final float scale = 0.03f;
		NormalLandmarkModel.Factory factory = new NormalLandmarkModel.Factory(conns, FLineSampler.INTERPOLATED_DERIVATIVE, 5, 9, scale);
		final ActiveShapeModel asm = ActiveShapeModel.trainModel(20, data, new PointDistributionModel.BoxConstraint(3), factory);

		final IndependentPair<PointList, FImage> initial = ASFDataset.readASF(new File(dir, "16-6m.asf"));
				
		VideoDisplay.createVideoDisplay(new AnimatedVideo<MBFImage>(new MBFImage(640,480, 3), 1) {
			Matrix pose = Matrix.identity(3, 3);
			PointList shape = initial.firstObject();
			FImage img = initial.secondObject();

			@Override
			protected void updateNextFrame(MBFImage frame) {
				frame.drawImage(img.toRGB(), 0, 0);
				frame.drawLines(conns.getLines(shape), 1, RGBColour.BLUE);

				IterationResult next = asm.performIteration(img, pose, shape);
				pose = next.pose;
				shape = next.shape;
			}
		});

		
	}
}